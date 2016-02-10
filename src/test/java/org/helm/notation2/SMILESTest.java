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
package org.helm.notation2;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;

import org.helm.chemtoolkit.CTKException;
import org.helm.chemtoolkit.CTKSmilesException;
import org.helm.chemtoolkit.ManipulatorFactory.ManipulatorType;
import org.helm.notation.MonomerException;
import org.helm.notation.MonomerLoadingException;
import org.helm.notation.NotationException;
import org.helm.notation.StructureException;
import org.helm.notation.tools.ComplexNotationParser;
import org.helm.notation2.exception.BuilderMoleculeException;
import org.helm.notation2.exception.ChemistryException;
import org.helm.notation2.exception.HELM2HandledException;
import org.helm.notation2.exception.ParserException;
import org.helm.notation2.parser.ConverterHELM1ToHELM2;
import org.helm.notation2.parser.ParserHELM2;
import org.helm.notation2.parser.exceptionparser.ExceptionState;
import org.helm.notation2.parser.notation.HELM2Notation;
import org.helm.notation2.parser.notation.polymer.PolymerNotation;
import org.jdom2.JDOMException;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * SMILESTest
 *
 * @author hecht
 */
public class SMILESTest {
  ParserHELM2 parser;

  public PolymerNotation getSimpleRNANotation() throws ParserException, JDOMException {
    String notation = "RNA1{P.R(A)[sP].RP.R(G)P.[LR]([5meC])}$$$$";
    return readNotation(notation).getListOfPolymers().get(0);
  }

  public PolymerNotation getInlineSmilesModAdenine() throws ParserException, JDOMException {
    String notation = "RNA1{R(C)P.R([C[N]1=CN=C(N)C2=C1N([*])C=N2 |$;;;;;;;;;_R1;;$,c:6,11,t:1,3|])[sP].RP.R(G)P.[LR]([5meC])P}$$$$";
    return readNotation(notation).getListOfPolymers().get(0);
  }

  public PolymerNotation getSimplePeptideNotation() throws ParserException, JDOMException {
    String notation = "PEPTIDE1{G.G.K.A.A.[seC]}$$$$";
    return readNotation(notation).getListOfPolymers().get(0);
  }

  public PolymerNotation getInlineSmilesPeptideNotation() throws ParserException, JDOMException {
    String notation = "PEPTIDE1{G.G.K.A.[C[C@H](N[*])C([*])=O |$;;;_R1;;_R2;$|].[seC]}$$$$";
    return readNotation(notation).getListOfPolymers().get(0);
  }

  public PolymerNotation getSimpleChemNotation() throws ParserException, JDOMException {
    String notation = "CHEM1{PEG2}$$$$";
    return readNotation(notation).getListOfPolymers().get(0);

  }

  public PolymerNotation getSmilesNotation() throws ParserException, JDOMException {
    String notation = "CHEM1{[*]OCCOCCOCCO[*] |$_R1;;;;;;;;;;;_R3$|}$$$$";
    return readNotation(notation).getListOfPolymers().get(0);

  }

  public PolymerNotation getRNANotationWithInline() throws ParserException, JDOMException {
    String notation =
        "RNA1{[C[C@@]1([*])O[C@H](CO[*])[C@@H](O[*])[C@H]1O |$;;_R3;;;;;_R1;;;_R2;;$|([Cc1nc2c(N)ncnc2n1[*] |$;;;;;;;;;;;_R1$|])[O[26P]([*])([*])=O |$;;_R1;_R2;$|]].R(C)P.R(T)P.R(G)}$$$$";
    return readNotation(notation).getListOfPolymers().get(0);
  }

  public PolymerNotation getRNANotationWithSalt() throws ParserException, JDOMException {
    String notation =
        "RNA1{P.R(A)[sP].R(A)[[Na+].[O-]P([*])([*])=O |$;;;_R1;_R2;$|].[LR]([5meC])}$$$$";
    return readNotation(notation).getListOfPolymers().get(0);

  }

  public PolymerNotation getChemNotationWithSalt() throws ParserException, JDOMException {
    String notation =
        "CHEM1{[[Na+].[O-]C1C=CC(=O)N1CC1CCC(CC1)C([*])=O |$;;;;;;;;;;;;;;;;_R1;$,c:2|]}$$$$";
    return readNotation(notation).getListOfPolymers().get(0);

  }

  public PolymerNotation getPeptideNotationWithSalt() throws ParserException, JDOMException {
    String notation =
        "PEPTIDE1{G.G.K.[[Na+].C[C@H](N[*])C([O-])[*] |$;;;;_R1;;;_R2$|].A.[seC]}$$$$";
    return readNotation(notation).getListOfPolymers().get(0);
  }

  // @Test
  public void testSMILES() throws ExceptionState, IOException, JDOMException, BuilderMoleculeException, CTKException, NotationException, ChemistryException {
    if (Chemistry.getInstance().getManipulatorType().equals(ManipulatorType.MARVIN)) {
      parser = new ParserHELM2();

      String test =
          "PEPTIDE1{D}|PEPTIDE2{E}|PEPTIDE3{L.R}|CHEM1{[MCC]}$PEPTIDE1,PEPTIDE2,1:R2-1:R3|PEPTIDE3,PEPTIDE1,2:R2-1:R3$$$";
      test += "V2.0";
      parser.parse(test);

      String smile = SMILES.getSMILESForAll(parser.getHELM2Notation());
      System.out.println(smile);
      String expectedResult = "OC(=O)C1CCC(CN2C(=O)C=CC2=O)CC1.[H]N[C@@H](CCC(=O)C(=O)[C@@H](N[H])CC(=O)C(=O)[C@H](CCCNC(N)=N)NC(=O)[C@@H](N[H])CC(C)C)C(O)=O";
      System.out.println(expectedResult);
      Assert.assertEquals(smile, expectedResult);
    }
  }

  // @Test
  public void testSMILESCanonical() throws ExceptionState, IOException, JDOMException, BuilderMoleculeException, CTKException, NotationException, ChemistryException {
    if (Chemistry.getInstance().getManipulatorType().equals(ManipulatorType.MARVIN)) {
      parser = new ParserHELM2();

      String test =
          "PEPTIDE1{D}|PEPTIDE2{E}|PEPTIDE3{L.R}|CHEM1{[MCC]}$PEPTIDE1,PEPTIDE2,1:R2-1:R3|PEPTIDE3,PEPTIDE1,2:R2-1:R3$$$";
      test += "V2.0";
      parser.parse(test);

      String canSmile = SMILES.getCanonicalSMILESForAll(parser.getHELM2Notation());
      Assert.assertEquals(canSmile, "OC(=O)C1CCC(CN2C(=O)C=CC2=O)CC1.CC(C)C[C@H](N)C(=O)N[C@@H](CCCNC(N)=N)C(=O)C(=O)C[C@H](N)C(=O)C(=O)CC[C@H](N)C(O)=O");
    }
  }

  public void testHELM1AgainstHELM2(String notation) throws ExceptionState, IOException, JDOMException, BuilderMoleculeException, CTKException, NotationException, MonomerException,
      StructureException, ChemistryException {
    parser = new ParserHELM2();
    String test = notation;
    test += "V2.0";
    parser.parse(test);
    String smiles = SMILES.getSMILESForAll(parser.getHELM2Notation());
    // ComplexNotationParser.getComplexPolymerSMILES(notation);
    SMILES.getCanonicalSMILESForAll(parser.getHELM2Notation());

  }

  @Test
  public void testGetSmilesPolymer() throws CTKSmilesException, BuilderMoleculeException, HELM2HandledException, CTKException, ParserException, JDOMException, NotationException, ChemistryException {
    SMILES.getCanonicalSMILESForPolymer(getSimpleRNANotation());

    SMILES.getCanonicalSMILESForPolymer(getInlineSmilesModAdenine());

    SMILES.getCanonicalSMILESForPolymer(getSimplePeptideNotation());

    SMILES.getCanonicalSMILESForPolymer(getInlineSmilesPeptideNotation());

    SMILES.getCanonicalSMILESForPolymer(getSimpleChemNotation());

    SMILES.getCanonicalSMILESForPolymer(getSmilesNotation());
    if (Chemistry.getInstance().getManipulatorType().equals(ManipulatorType.MARVIN)) {
      SMILES.getCanonicalSMILESForPolymer(getRNANotationWithSalt());
    }

  }

  @Test
  public void testSelfCycle() throws ExceptionState, IOException, JDOMException, BuilderMoleculeException, CTKException, NotationException, MonomerException, StructureException, ChemistryException {
    // backbone cyclic peptide
    String notation = "PEPTIDE1{A.A.G.K}$PEPTIDE1,PEPTIDE1,1:R1-4:R2$$$";
    testHELM1AgainstHELM2(notation);

    // backbone cyclic RNA
    notation = "RNA1{R(C)P.RP.R(A)P.RP.R(A)P.R(U)P}$RNA1,RNA1,1:R1-16:R2$$$";
    testHELM1AgainstHELM2(notation);

    // cyclic chem
    notation = "CHEM1{SS3}|CHEM2{SS3}$CHEM1,CHEM2,1:R1-1:R1|CHEM1,CHEM2,1:R2-1:R2$$$";
    testHELM1AgainstHELM2(notation);

    // peptide-chem cycles
    notation = "PEPTIDE1{H.H.E.E.E}|CHEM1{SS3}|CHEM2{EG}$PEPTIDE1,CHEM2,5:R2-1:R2|CHEM2,CHEM1,1:R1-1:R2|PEPTIDE1,CHEM1,1:R1-1:R1$$$";
    testHELM1AgainstHELM2(notation);

    // multiple peptide-chem cycles
    notation =
        "PEPTIDE1{E.E.E.E.E}|PEPTIDE2{E.D.D.I.A.C.D.E}|CHEM1{SS3}|CHEM2{SS3}|CHEM3{SS3}$PEPTIDE2,CHEM2,8:R2-1:R1|PEPTIDE1,CHEM3,5:R2-1:R2|PEPTIDE1,CHEM1,1:R1-1:R1|PEPTIDE2,CHEM3,1:R1-1:R1|CHEM1,CHEM2,1:R2-1:R2$$$";
    testHELM1AgainstHELM2(notation);
  }

  @Test(expectedExceptions = BuilderMoleculeException.class)
  public void testChiralCenter() throws ExceptionState, IOException, JDOMException, BuilderMoleculeException, CTKException, NotationException, MonomerException, StructureException,
      ChemistryException {
    // backbone and branch cyclic RNA
    String notation = "RNA1{R(C)P.RP.R(A)P.RP.R(A)P.R(U)P}$RNA1,RNA1,4:R3-9:R3$$$";
    testHELM1AgainstHELM2(notation);

  }

  private HELM2Notation readNotation(String notation) throws ParserException, JDOMException {
    /* HELM1-Format -> */
    if (!(notation.contains("V2.0"))) {
      notation = new ConverterHELM1ToHELM2().doConvert(notation);
    }
    /* parses the HELM notation and generates the necessary notation objects */
    ParserHELM2 parser = new ParserHELM2();
    try {
      parser.parse(notation);
    } catch (ExceptionState | IOException e) {
      throw new ParserException(e.getMessage());
    }
    return parser.getHELM2Notation();
  }

  @Test
  public void testInlineNotation() throws CTKSmilesException, BuilderMoleculeException, CTKException, NotationException, ParserException, JDOMException, MonomerLoadingException, IOException,
      MonomerException, StructureException, ChemistryException {

    String notation = "PEPTIDE1{A.G.G.G.C.C.K.K.K.K}|CHEM1{MCC}$PEPTIDE1,CHEM1,10:R3-1:R1$$$";
    String smiles = SMILES.getCanonicalSMILESForAll(readNotation(notation));

    // replaced A with Smiles String
    notation = "PEPTIDE1{[C[C@H](N[*])C([*])=O |$;;;_R1;;_R2;$|].G.G.G.C.C.K.K.K.K}|CHEM1{MCC}$PEPTIDE1,CHEM1,10:R3-1:R1$$$";
    String smilesInline = SMILES.getCanonicalSMILESForAll(readNotation(notation));
    AssertJUnit.assertEquals(smiles, smilesInline);

    // replaced A with slightly modified A
    notation = "PEPTIDE1{[C[C@H](N[*])C(=O)C[*] |$;;;_R1;;;;_R2$|].G.G.G.C.C.K.K.K.K}|CHEM1{MCC}$PEPTIDE1,CHEM1,10:R3-1:R1$$$";

    smilesInline = ComplexNotationParser.getComplexPolymerSMILES(notation);
    System.out.println(smilesInline);
    System.out.println(SMILES.getCanonicalSMILESForAll(readNotation(notation)));

  }

}
