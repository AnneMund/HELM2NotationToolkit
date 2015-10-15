package org.helm.notation.tools;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.helm.notation.MonomerException;
import org.helm.notation.MonomerStore;
import org.helm.notation.NotationException;
import org.helm.notation.StructureException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import chemaxon.marvin.plugin.PluginException;

public class xHelmNotationParserTest {

	private Element getXHELMRootElement(String resource) throws JDOMException,
			IOException {

		InputStream in = this.getClass().getResourceAsStream(resource);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);

		return doc.getRootElement();
	}

	@Test
	public void testParseXHelmNotation() throws JDOMException, IOException,
			MonomerException, NotationException, StructureException,
			ClassNotFoundException, PluginException {

		Element xHELMRootElement = getXHELMRootElement("resources/PeptideLinkerNucleotide.xhelm");
		String helmString = xHelmNotationParser
				.getComplexNotationString(xHELMRootElement);
		// read monomers to store
		MonomerStore store = xHelmNotationParser
				.getMonomerStore(xHELMRootElement);

		AssertJUnit.assertEquals(
				"RNA1{[am6]P.R(C)P.R(U)P.R(U)P.R(G)P.R(A)P.R(G)P.R(G)}|PEPTIDE1{[aaa].C.G.K.E.D.K.R}|CHEM1{SMCC}$PEPTIDE1,CHEM1,2:R3-1:R2|RNA1,CHEM1,1:R1-1:R1$$$",
				helmString);

		AssertJUnit.assertTrue(ComplexNotationParser.validateComplexNotation(helmString,
				store));

		String canonicalNotation = ComplexNotationParser.getCanonicalNotation(
				helmString, true, store);

		AssertJUnit.assertEquals(
				"CHEM1{SMCC}|PEPTIDE1{[aaa].C.G.K.E.D.K.R}|RNA1{[am6]P.R(C)P.R(U)P.R(U)P.R(G)P.R(A)P.R(G)P.R(G)}$CHEM1,PEPTIDE1,1:R2-2:R3|CHEM1,RNA1,1:R1-1:R1$$$",
				canonicalNotation);

		xHELMRootElement = getXHELMRootElement("resources/simple.xhelm");
		helmString = xHelmNotationParser
				.getComplexNotationString(xHELMRootElement);

		store = xHelmNotationParser.getMonomerStore(xHELMRootElement);

		AssertJUnit.assertEquals("PEPTIDE1{G.K.A.[A_copy]}$$$$", helmString);

		AssertJUnit.assertTrue(ComplexNotationParser.validateComplexNotation(helmString,
				store));

		/*
		 * Document
		 * doc=xHelmNotationExporter.buildXHelmDocument(helmString,store);
		 * 
		 * XMLOutputter xmlOutput = new XMLOutputter(); // display nice
		 * xmlOutput.setFormat(Format.getPrettyFormat());
		 * 
		 * 
		 * String xml=xmlOutput.outputString(doc);
		 * 
		 * System.out.println(xml);
		 */

		xHELMRootElement = getXHELMRootElement("resources/InlineSmiles.xhelm");
		helmString = xHelmNotationParser
				.getComplexNotationString(xHELMRootElement);

		store = xHelmNotationParser.getMonomerStore(xHELMRootElement);

		AssertJUnit.assertEquals(
				"PEPTIDE1{A.C.A.C.G.K.E.E}|PEPTIDE2{A.C.A.C.G.K.E.E}|CHEM1{[*]OCCOCCOCCO[*] |$_R1;;;;;;;;;;;_R3$|}|CHEM2{PEG2}$PEPTIDE1+PEPTIDE2,CHEM1,generic:K-1:R1|PEPTIDE1+PEPTIDE2,CHEM2,generic:Q1+Q2-1:R1$$$",
				helmString);

		AssertJUnit.assertTrue(ComplexNotationParser.validateComplexNotation(helmString,
				store));

		xHELMRootElement = getXHELMRootElement("resources/RNAWithInline.xhelm");
		helmString = xHelmNotationParser
				.getComplexNotationString(xHELMRootElement);

		store = xHelmNotationParser.getMonomerStore(xHELMRootElement);

		AssertJUnit.assertEquals(
				"RNA1{[C[C@@]1([*])O[C@H](CO[*])[C@@H](O[*])[C@H]1O |$;;_R3;;;;;_R1;;;_R2;;$|](A)P.RP.[C[C@@]1([*])O[C@H](CO[*])[C@@H](O[*])[C@H]1O |$;;_R3;;;;;_R1;;;_R2;;$|](T)P.R([Cc1nc2c(nc(N)[nH]c2=O)n1[*] |$;;;;;;;;;;;;_R1$|])P.R([Cc1cc(N)nc(=O)n1[*] |$;;;;;;;;;_R1$|])}$$$$",
				helmString);

		AssertJUnit.assertTrue(ComplexNotationParser.validateComplexNotation(helmString,
				store));

	}

	@Test
	public void testQRPeptide() throws JDOMException, IOException,
			MonomerException, NotationException, StructureException,
			ClassNotFoundException {
		Element xHELMRootElement = getXHELMRootElement("resources/qr_peptide.xhelm");
		String helmString = xHelmNotationParser
				.getComplexNotationString(xHELMRootElement);

		MonomerStore store = xHelmNotationParser
				.getMonomerStore(xHELMRootElement);

		AssertJUnit.assertEquals("PEPTIDE1{[QR]}$$$$", helmString);

		AssertJUnit.assertTrue(ComplexNotationParser.validateComplexNotation(helmString,
				store));
	}

	@Test
	public void testXHelmWithInlineSmiles() throws JDOMException, IOException,
			MonomerException, NotationException, StructureException,
			ClassNotFoundException {
		Element xHELMRootElement = getXHELMRootElement("resources/simpleWithInlineSmiles.xhelm");

		String helmString = xHelmNotationParser
				.getComplexNotationString(xHELMRootElement);
		MonomerStore store = xHelmNotationParser
				.getMonomerStore(xHELMRootElement);

		boolean valid = ComplexNotationParser.validateComplexNotation(
				helmString, store);
		AssertJUnit.assertTrue(valid);
		String smiles = ComplexNotationParser.getComplexPolymerSMILES(
				helmString, store);

		AssertJUnit.assertEquals(
				"[H]NCCCC[C@H](NC(=O)CN[H])C(=O)N[C@@H](C)C(=O)CN[C@@H](C)C(O)=O",
				smiles);

	}

	@Test
	public void testInlineSmilesChem() throws JDOMException, IOException,
			MonomerException, NotationException, StructureException,
			ClassNotFoundException {
		Element xHELMRootElement = getXHELMRootElement("resources/inlineSmilesChem.xhelm");

		String helmString = xHelmNotationParser
				.getComplexNotationString(xHELMRootElement);
		MonomerStore store = xHelmNotationParser
				.getMonomerStore(xHELMRootElement);

		boolean valid = ComplexNotationParser.validateComplexNotation(
				helmString, store);
		AssertJUnit.assertTrue(valid);

	}

	@Test
	public void testPeptideLinkerWithChemInline() throws JDOMException,
			IOException, MonomerException, NotationException,
			StructureException, ClassNotFoundException {
		Element xHELMRootElement = getXHELMRootElement("resources/PeptideLinkerWithChemInline.xhelm");

		String helmString = xHelmNotationParser
				.getComplexNotationString(xHELMRootElement);
		MonomerStore store = xHelmNotationParser
				.getMonomerStore(xHELMRootElement);

		boolean valid = ComplexNotationParser.validateComplexNotation(
				helmString, store);
		AssertJUnit.assertTrue(valid);

	}

	@Test
	public void testInlineSmilesSinglePeptide() throws JDOMException,
			IOException, MonomerException, NotationException,
			StructureException, ClassNotFoundException {
		Element xHELMRootElement = getXHELMRootElement("resources/InlineSmilesSinglePeptide.xhelm");

		String helmString = xHelmNotationParser
				.getComplexNotationString(xHELMRootElement);
		MonomerStore store = xHelmNotationParser
				.getMonomerStore(xHELMRootElement);

		boolean valid = ComplexNotationParser.validateComplexNotation(
				helmString, store);
		AssertJUnit.assertTrue(valid);

	}

	@Test
	public void testXHelmValidation() throws JDOMException, IOException,
			MonomerException {

		Element xHELMRootElement = getXHELMRootElement("resources/bad.xhelm");

		String helmString = xHelmNotationParser
				.getComplexNotationString(xHELMRootElement);
		MonomerStore store = xHelmNotationParser
				.getMonomerStore(xHELMRootElement);

		try {
			ComplexNotationParser.validateComplexNotation(helmString, store);
			Assert.fail("xHelm document is not valid-should have thrown exception");
		}

		catch (Exception e) {
			AssertJUnit.assertTrue("xHelm doc validation failed as expected", true);
		}

	}

	@BeforeClass
	public static void init() {

	}

	@AfterClass
	public static void finish() {

	}

}
