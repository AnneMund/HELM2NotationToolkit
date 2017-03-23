/**
 * *****************************************************************************
 * Copyright C 2015, The Pistoia Alliance
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *****************************************************************************
 */

package org.helm.notation2.tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.helm.chemtoolkit.AbstractMolecule;
import org.helm.chemtoolkit.CTKException;
import org.helm.chemtoolkit.IAtomBase;
import org.helm.chemtoolkit.IBondBase;
import org.helm.notation2.Attachment;
import org.helm.notation2.Chemistry;
import org.helm.notation2.Monomer;
import org.helm.notation2.MonomerFactory;
import org.helm.notation2.exception.ChemistryException;
import org.helm.notation2.exception.EncoderException;
import org.helm.notation2.exception.MonomerException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MonomerParser
 *
 * @author hecht
 */
public class MonomerParser {

  /** The Logger for this class */
  private static final Logger LOG = LoggerFactory.getLogger(MonomerParser.class);

  public static final String MONOMER_ELEMENT = "Monomer";

  public static final String MONOMER_ID_ELEMENT = "MonomerID";

  public static final String MONOMER_SMILES_ELEMENT = "MonomerSmiles";

  public static final String MONOMER_MOL_FILE_ELEMENT = "MonomerMolFile";

  public static final String MONOMER_TYPE_ELEMENT = "MonomerType";

  public static final String POLYMER_TYPE_ELEMENT = "PolymerType";

  public static final String NATURAL_ANALOG_ELEMENT = "NaturalAnalog";

  public static final String MONOMER_NAME_ELEMENT = "MonomerName";

  public static final String ATTACHEMENTS_LIST_ELEMENT = "AttachmentList";

  public static final String ATTACHEMENTS_ELEMENT = "Attachments";

  public static final String ATTACHEMENT_ELEMENT = "Attachment";

  public static final String ATTACHEMENT_ID_ELEMENT = "AttachmentID";

  public static final String ATTACHEMENT_LABEL_ELEMENT = "AttachmentLabel";

  public static final String CAP_GROUP_NAME_ELEMENT = "CapGroupName";

  public static final String CAP_GROUP_SMILES_ELEMENT = "CapGroupSmiles";

  private static List<String> polymerTypes = new ArrayList<String>();
  
  protected static final String SMILES_EXTENSION_SEPARATOR_REGEX = "\\|";

  static {
    polymerTypes = Arrays.asList(Monomer.SUPPORTED_POLYMER_TYPES);
  }

  /**
   * Convert ATTACHMENT element to Attachment object
   *
   * @param attachment element
   * @return Attachment
   */
  public static Attachment getAttachment(Element attachment) {

    Namespace ns = attachment.getNamespace();

    Attachment att = new Attachment();
    att.setAlternateId(attachment.getChildText(ATTACHEMENT_ID_ELEMENT, ns));
    att.setLabel(attachment.getChildText(ATTACHEMENT_LABEL_ELEMENT, ns));
    att.setCapGroupName(attachment.getChildText(CAP_GROUP_NAME_ELEMENT, ns));
    att.setCapGroupSMILES(attachment.getChildText(CAP_GROUP_SMILES_ELEMENT, ns));

    return att;
  }

  /**
   * This method converts Attachment to ATTACHMENT XML element
   *
   * @param att -- Attachment
   * @return Element
   */
  public static Element getAttachementElement(Attachment att) {

    Element attachment = new Element(ATTACHEMENT_ELEMENT);

    if (null != att.getAlternateId() && att.getAlternateId().length() > 0) {
      Element e = new Element(ATTACHEMENT_ID_ELEMENT);
      e.setText(att.getAlternateId());
      attachment.getChildren().add(e);
    }

    if (null != att.getLabel() && att.getLabel().length() > 0) {
      Element e = new Element(ATTACHEMENT_LABEL_ELEMENT);
      e.setText(att.getLabel());
      attachment.getChildren().add(e);
    }

    if (null != att.getCapGroupName() && att.getCapGroupName().length() > 0) {
      Element e = new Element(CAP_GROUP_NAME_ELEMENT);
      e.setText(att.getCapGroupName());
      attachment.getChildren().add(e);
    }

    if (null != att.getCapGroupSMILES()
        && att.getCapGroupSMILES().length() > 0) {
      Element e = new Element(CAP_GROUP_SMILES_ELEMENT);
      e.setText(att.getCapGroupSMILES());
      attachment.getChildren().add(e);
    }

    return attachment;
  }

  /**
   * This method validates Attachment by the following rules
   * Attachment must have unique ID 
   * cap group SMILES must be valid 
   * cap group SMILES must contain one R group
   * R group in SMILES must match R group label
   *
   * @param attachment given attachment
   * @return true or false
   * @throws org.helm.notation2.exception.MonomerException if attachment is not valid
   * @throws java.io.IOException IO error
   * @throws ChemistryException if chemistry engine can not be initialized
   */
  public static boolean validateAttachement(Attachment attachment)
      throws MonomerException, IOException, ChemistryException {

    String alternateId = attachment.getAlternateId();
    if (null == alternateId) {
      throw new MonomerException("Attachment must have unique ID");
    }

    String smiles = attachment.getCapGroupSMILES();
    if (null != smiles) {

      if (!Chemistry.getInstance().getManipulator().validateSMILES(smiles)) {
        throw new MonomerException(
            "Attachment cap group SMILES is invalid");
      }

      List<String> labels = getAttachmentLabels(smiles);
      if (null == labels || labels.size() != 1) {
        throw new MonomerException(
            "Attachment must have one R group in SMILES");
      }

      if (!(labels.get(0).equals(attachment.getLabel()))) {
        throw new MonomerException(
            "R group in monomer SMILES and R group label must match");
      }
    }
    return true;
  }

  /**
   * Convert monomer element to Monomer object
   *
   * @param monomer element
   * @return Monomer
   * @throws MonomerException if element is not a valid monomer
   */
  public static Monomer getMonomer(Element monomer) throws MonomerException {
    Monomer m = new Monomer();
    Namespace ns = monomer.getNamespace();
    m.setAlternateId(monomer.getChildText(MONOMER_ID_ELEMENT, ns));
    m.setCanSMILES(monomer.getChildText(MONOMER_SMILES_ELEMENT, ns));
    String encodedMolfile = monomer.getChildText(MONOMER_MOL_FILE_ELEMENT, ns);

    String molfile = null;
    try {
      molfile = MolfileEncoder.decode(encodedMolfile);
    } catch (EncoderException ex) {
      throw new MonomerException("Invalid monomer molfile");
    }
    m.setMolfile(molfile);
    m.setMonomerType(monomer.getChildText(MONOMER_TYPE_ELEMENT, ns));
    m.setPolymerType(monomer.getChildText(POLYMER_TYPE_ELEMENT, ns));
    m.setNaturalAnalog(monomer.getChildText(NATURAL_ANALOG_ELEMENT, ns));
    m.setName(monomer.getChildText(MONOMER_NAME_ELEMENT, ns));
    Element attachmentElement = monomer.getChild(ATTACHEMENTS_ELEMENT, ns);

    if (null != attachmentElement) {
      List attachments = attachmentElement.getChildren(ATTACHEMENT_ELEMENT, ns);
      List<Attachment> l = new ArrayList<Attachment>();
      Iterator i = attachments.iterator();
      while (i.hasNext()) {
        Element attachment = (Element) i.next();
        Attachment att = getAttachment(attachment);
        l.add(att);
      }
      m.setAttachmentList(l);
    }
    return m;
  }

  /**
   * This method converts Monomer to MONOMER XML element
   *
   * @param monomer given monomer
   * @return Element
   * @throws MonomerException if monomer is not valid
   */
  public static Element getMonomerElement(Monomer monomer)
      throws MonomerException {
    Element element = new Element(MONOMER_ELEMENT);

    if (null != monomer.getAlternateId()) {
      Element e = new Element(MONOMER_ID_ELEMENT);
      e.setText(monomer.getAlternateId());
      element.getChildren().add(e);
    }

    if (null != monomer.getCanSMILES()) {
      Element e = new Element(MONOMER_SMILES_ELEMENT);
      e.setText(monomer.getCanSMILES());
      element.getChildren().add(e);
    }

    if (null != monomer.getMolfile()) {
      Element e = new Element(MONOMER_MOL_FILE_ELEMENT);
      String encodedMolfile = null;
      try {
        encodedMolfile = MolfileEncoder.encode(monomer.getMolfile());
      } catch (EncoderException ex) {
        throw new MonomerException("Invalid monomer molfile");
      }
      // CDATA cdata = new CDATA(monomer.getMolfile());
      // e.setContent(cdata);
      e.setText(encodedMolfile);
      element.getChildren().add(e);
    }

    if (null != monomer.getMonomerType()) {
      Element e = new Element(MONOMER_TYPE_ELEMENT);
      e.setText(monomer.getMonomerType());
      element.getChildren().add(e);
    }

    if (null != monomer.getPolymerType()) {
      Element e = new Element(POLYMER_TYPE_ELEMENT);
      e.setText(monomer.getPolymerType());
      element.getChildren().add(e);
    }

    if (null != monomer.getNaturalAnalog()) {
      Element e = new Element(NATURAL_ANALOG_ELEMENT);
      e.setText(monomer.getNaturalAnalog());
      element.getChildren().add(e);
    }

    if (null != monomer.getName()) {
      Element e = new Element(MONOMER_NAME_ELEMENT);
      e.setText(monomer.getName());
      element.getChildren().add(e);
    }

    List<Attachment> l = monomer.getAttachmentList();
    if (null != l && l.size() > 0) {
      Element attachments = new Element(ATTACHEMENTS_ELEMENT);

      for (int i = 0; i < l.size(); i++) {
        Attachment att = l.get(i);
        Element attachment = getAttachementElement(att);
        attachments.getChildren().add(attachment);
      }
      element.getChildren().add(attachments);
    }

    return element;
  }

  public static List<Monomer> getMonomerList(String monomerXMLString)
      throws JDOMException, IOException, MonomerException, CTKException, ChemistryException {
    List<Monomer> l = new ArrayList<Monomer>();
    if (null != monomerXMLString && monomerXMLString.length() > 0) {
      SAXBuilder builder = new SAXBuilder();
      ByteArrayInputStream bais = new ByteArrayInputStream(
          monomerXMLString.getBytes());
      Document doc = builder.build(bais);
      Element root = doc.getRootElement();

      List monomers = root.getChildren();
      Iterator it = monomers.iterator();
      while (it.hasNext()) {
        Element monomer = (Element) it.next();
        Monomer m = getMonomer(monomer);
        if (MonomerParser.validateMonomer(m)) {
          l.add(m);
        }
      }
    }
    return l;
  }

  public static Monomer getMonomer(String monomerXMLString)
      throws JDOMException, IOException, MonomerException {
    Monomer m = null;
    if (monomerXMLString != null && monomerXMLString.length() > 0) {
      SAXBuilder builder = new SAXBuilder();
      ByteArrayInputStream bais = new ByteArrayInputStream(
          monomerXMLString.getBytes());
      Document doc = builder.build(bais);
      Element root = doc.getRootElement();
      m = getMonomer(root);
    }
    return m;

  }

  /**
   * This methods checks the validity of the monomer based on the following
   * rules
   * monomer cannot be null
   * polymer type cannot be null
   * and must be one of the defined polymer type
   * monomer type cannot be null and must be one of the defined monomer type for a given polymer
   * type
   * Monomer ID cannot be null 
   * structure cannot be null for non-chemical type monomer
   * structure SMILES must be valid
   * attachment labels on monomer must be unique 
   * Attachment number on SMILES must match attachment List size 
   * Each attachment in attachment list must be valid (call validateAttachment())
   * Attachment labels on monomer must match atachment label on attachment
   * list
   * For non-chemical type monomers, modified monomer (ID length
   * greater than 1) must have natural analog
   * All monomers must have at least one attachment
   *
   * @param monomer given monomer
   * @return true or false
   * @throws org.helm.notation2.exception.MonomerException if monomer is not valid
   * @throws java.io.IOException IO error
   * @throws CTKException general ChemToolKit exception passed to HELMToolKit
   * @throws ChemistryException if chemistry engine can not be initialized
   */
  public static boolean validateMonomer(Monomer monomer)
      throws MonomerException, IOException, CTKException, ChemistryException {

    if (null == monomer) {
      throw new MonomerException("Monomer is null");
    } else {
      String polymerType = monomer.getPolymerType();
      if (null == polymerType) {
        throw new MonomerException(
            "Monomer has no polymer type defined");
      } else if (!polymerTypes.contains(polymerType)) {
        throw new MonomerException("Unknown polymer type '"
            + polymerType + "'");
      }

      String monomerType = monomer.getMonomerType();
      if (null == monomerType) {
        throw new MonomerException(
            "Monomer has no monomer type defined");
      } else {
        if (polymerType.equals(Monomer.CHEMICAL_POLYMER_TYPE)) {
          if (!monomerType.equals(Monomer.UNDEFINED_MOMONER_TYPE)) {
            throw new MonomerException(
                "Valid monomer type for chemical structures can only be '"
                    + Monomer.UNDEFINED_MOMONER_TYPE + "'");
          }
        } else {
          if (!(monomerType.equals(Monomer.BACKBONE_MOMONER_TYPE) || monomerType.equals(Monomer.BRANCH_MOMONER_TYPE))) {
            throw new MonomerException(
                "Valid monomer type for simple polymer can only be '"
                    + Monomer.BACKBONE_MOMONER_TYPE
                    + "' or '"
                    + Monomer.BRANCH_MOMONER_TYPE + "'");
          }
        }
      }

      String alternateId = monomer.getAlternateId();
      if (null == alternateId || alternateId.length() == 0) {
        throw new MonomerException("Monomer has no monomerID defined");
      }
      String smiles = monomer.getCanSMILES();
      String molfile = monomer.getMolfile();
      List<Attachment> attachments = monomer.getAttachmentList();

      if (!polymerType.equals(Monomer.CHEMICAL_POLYMER_TYPE)) {
        if (null == smiles || null == molfile || null == attachments
            || attachments.size() == 0) {
          throw new MonomerException(
              "Monomers for specific polymer type must have structure info");
        }
      }

      String errorNote = alternateId + " (" + polymerType + ")";
      if (null != smiles && smiles.length() > 0) {

        boolean validSmiles = Chemistry.getInstance().getManipulator().validateSMILES(smiles);
        if (!validSmiles) {
          throw new MonomerException("Monomer SMILES must be valid: "
              + errorNote);
        }
        List<String> attachmentLabels = getAttachmentLabels(smiles);
        boolean unique = areAttachmentLabelsUnique(attachmentLabels);
        if (!unique) {
          throw new MonomerException(
              "Attachment labels on monomer must be unique: "
                  + errorNote);
        }
        if (attachmentLabels.size() != attachments.size()) {
          throw new MonomerException(
              "Attachment label number on monomer must match attachment number: "
                  + errorNote);
        }
        for (int i = 0; i < attachments.size(); i++) {
          Attachment att = attachments.get(i);
          validateAttachement(att);
        }

        for (int i = 0; i < attachmentLabels.size(); i++) {
          String label = attachmentLabels.get(i);
          boolean found = false;
          for (int j = 0; j < attachments.size(); j++) {
            Attachment att = attachments.get(j);
            if (att.getAlternateId().startsWith(label)) {
              found = true;
              break;
            }
          }

          if (!found) {
            throw new MonomerException(
                "Attachment label in SMILES is not found in attachment list: "
                    + errorNote);
          }
        }
      }

      if (monomer.getAlternateId().length() > 0
          && !(monomer.getPolymerType().equals(Monomer.CHEMICAL_POLYMER_TYPE))) {
        String naturalAnalog = monomer.getNaturalAnalog();

        if (null == naturalAnalog) {
          throw new MonomerException(
              "Modified monomer must have natural analog defined: "
                  + errorNote);
        } else {
          if (naturalAnalog.length() != 1) {
            throw new MonomerException(
                "Natural analog must be single letter: "
                    + errorNote);
          }
        }
      }

      if (monomer.getAttachmentList() == null
          || monomer.getAttachmentList().size() == 0) {
        throw new MonomerException(
            "Monomer must have at least one attachment: "
                + errorNote);
      }

      // make sure R group can only be connected to one atom via single
      // achiral bond
      // MolBond javadoc: getType()Gets the bond type. Possible values: 1
      // (single), 2 (double), 3 (triple), coordinate, conjugated and
      // query bond types.
      if (null != smiles && smiles.length() > 0) {
        AbstractMolecule molecule = Chemistry.getInstance().getManipulator().getMolecule(smiles, null);
        List<String> attachmentLabels = getAttachmentLabels(smiles);
        for (int i = 0; i < attachmentLabels.size(); i++) {
          String rgroupId = attachmentLabels.get(i).substring(1);
          IAtomBase atom = null;
          atom = molecule.getRGroupAtom(Integer.parseInt(rgroupId), true);
          if(atom == null){
        	  throw new MonomerException("Molecule does not contain the specified Rgroup");
          }
          if (atom.getIBondCount() != 1) {
            throw new MonomerException(
                "R group can only connect with one atom in monomer: "
                    + errorNote);
          } else {
            IBondBase bond = atom.getIBond(0);
            if (bond.getType() != 1)
              throw new MonomerException(
                  "R group can only connect with another atom via single bond in monomer: "
                      + errorNote);
          }
        }
      }
    }

    return true;
  }

  /**
   * This methods return the list of R groups in the extended SMILES string
   *
   * @param smiles given smiles
   * @return string list containing r groups of the smiles
   */
  private static List<String> getAttachmentLabels(String smiles) {


    /*int start = 0;
    int rPos = extendedSmiles.indexOf("R");
    StringBuffer sb = new StringBuffer();
    while (rPos > 0) {
      rPos++;
      String nextLetter = extendedSmiles.substring(rPos, rPos + 1);
      if (nextLetter.matches("[0-9]")) {
        sb.append(nextLetter);
      } else {
        labels.add("R" + sb.toString());
        sb = new StringBuffer();
        start = rPos + 1;
        rPos = extendedSmiles.indexOf("R", start);
      }
    }*/
    
    String  extendedSmiles = getExtension(smiles);
    List<String> list = new ArrayList<>();
    if (extendedSmiles != null) {
      Integer currIndex = 0;
      char[] items = extendedSmiles.toCharArray();
      List<Integer> indexes = new ArrayList<Integer>();

      while (extendedSmiles.indexOf("_R", currIndex) > 0) {
        currIndex = extendedSmiles.indexOf("_R", currIndex);
        indexes.add(currIndex);
        currIndex++;
      }

      for (int k = 0; k < items.length; k++) {
        if (items[k] == 'R') {
          indexes.add(currIndex + k);
          currIndex++;
        }
      }

      String[] tokens = extendedSmiles.split("R", -1);
      if (tokens.length > 1) {
        for (int i = 1; i < tokens.length; i++) {
          String token = tokens[i];
          char[] chars = token.toCharArray();
          String numbers = "";
          for (int j = 0; j < chars.length; j++) {
            String letter = String.valueOf(chars[j]);
            if (letter.matches("[0-9]")) {
              numbers += letter;
            } else {
              break;
            }
          }

          if (numbers.length() > 0) {
            list.add("R" + numbers);
          }
        }
      }
    }
    
    if(extendedSmiles == null){
    	Pattern pattern = Pattern.compile("\\[\\*:([1-9]\\d*)\\]|\\[\\w+:([1-9]\\d*)\\]");
    	Matcher matcher = pattern.matcher(smiles);

  	  
  	  while(matcher.find()){
  		  String replace = "";
  		  if(matcher.group(1) != null){
  			  replace = matcher.group(1);
  		  }
  		  if(matcher.group(2) != null){
  			  replace = matcher.group(2);
  		  }
  		  
  		  list.add("R" + replace);
  	  }
    }

    return list;
  }

  private static String getExtension(String smiles) {
	  String result = null;
	    try {

	      String[] components = smiles.split(SMILES_EXTENSION_SEPARATOR_REGEX);
	      result = components[1];
	    } catch (ArrayIndexOutOfBoundsException e) {
	// not extended SMILES
	    }

	    return result;
}

/**
   * This method checks if strings in a list are unique
   *
   * @param labels list of attachments labels
   * @return true or false
   */
  private static boolean areAttachmentLabelsUnique(List<String> labels) {
    Map<String, String> map = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
    for (int i = 0; i < labels.size(); i++) {
      map.put(labels.get(i), labels.get(i));
    }
    if (labels.size() == map.size()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * This method checks if attachment label is in the format of R#, where # is a
   * number
   *
   * @param label attachment label
   * @throws org.helm.notation2.exception.MonomerException if label is not valid
   */
  public static void validateAttachmentLabel(String label)
      throws MonomerException {

    if (label.equalsIgnoreCase(Attachment.PAIR_ATTACHMENT)) {
      return;
    }
    char[] chars = label.toCharArray();

    if (!(String.valueOf(chars[0])).equals("R")) {
      throw new MonomerException("Invalid Attachment Label format");
    }
    for (int i = 1; i < chars.length; i++) {
      char c = chars[i];
      if (!(String.valueOf(c)).matches("[0-9]")) {
        throw new MonomerException("Invalid Attachment Label format");
      }
    }
  }

  public static void fillAttachmentInfo(Attachment att)
      throws MonomerException, IOException, JDOMException, ChemistryException, CTKException {
    Map<String, Attachment> attachmentMap = MonomerFactory.getInstance().getAttachmentDB();

    Attachment attach = attachmentMap.get(att.getAlternateId());
    att.setLabel(attach.getLabel());
    att.setCapGroupSMILES(attach.getCapGroupSMILES());
    att.setCapGroupName(attach.getCapGroupName());
  }

}
