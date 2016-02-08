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

import java.io.IOException;

import org.helm.notation2.exception.AnalogSequenceException;
import org.helm.notation2.exception.ChemistryException;
import org.helm.notation2.exception.FastaFormatException;
import org.helm.notation2.parser.ConverterHELM1ToHELM2;
import org.helm.notation2.parser.ParserHELM2;
import org.helm.notation2.parser.exceptionparser.ExceptionState;
import org.helm.notation2.parser.exceptionparser.NotationException;
import org.jdom2.JDOMException;
import org.testng.annotations.Test;

public class FastaFormatTest {
  @Test
  public void testReadFastaPEPTIDE() throws FastaFormatException {
    FastaFormat.generatePeptidePolymersFromFASTAFormatHELM1(">gi|5524211|gb|AAD44166.1| cytochrome b [Elephas maximus maximus]\nLCLYTHIGRNIYYGSYLYSETWNTGIMLLLITMATAFMGYVLPWGQMSFWGATVITNLFSAIPYIGTNLV\nEWIWGGFSVDKATLNRFFAFHFILPFTMVALAGVHLTFLHETGSNNPLGLTSDSDKIPFHPYYTIKDFLG");
  }

  @Test(expectedExceptions = FastaFormatException.class)
  public void testReadFastaPEPTIDEWithException() throws FastaFormatException {
    FastaFormat.generatePeptidePolymersFromFASTAFormatHELM1(">gi|5524211|gb|AAD44166.1| cytochrome b [Elephas maximus maximus]\nLCLYTHIGRN_YYGSYLYSETWNTGIMLLLITMATAFMGYVLPWGQMSFWGATVITNLFSAIPYIGTNLV\nEWIWGGFSVDKATLNRFFAFHFILPFTMVALAGVHLTFLHETGSNNPLGLTSDSDKIPFHPYYTIKDFLG");
  }

  @Test
  public void testReadFastaRNA() throws FastaFormatException, NotationException, IOException, JDOMException {
    FastaFormat.generateRNAPolymersFromFastaFormatHELM1(">Seq1 [organism=Carpodacus mexicanus] C.mexicanus clone 6b actin (act) mRNA, partial cds\nCCTTTATCTAATCTTTGGAGCATGAGCTGGCATAGTTGGAACCGCCCTCAGCCTCCTCATCCGTGCAGAA\nCTTGGACAACCTGGAACTCTTCTAGGAGACGACCAAATTTACAATGTAATCGTCACTGCCCACGCCTTCG\nTAATAATTTTCTTTATAGTAATACCAATCATGATCGGTGGTTTCGGAAACTGACTAGTCCCACTCATAAT\nCGGCGCCCCCGACATAGCATTCCCCCGTATAAACAACATAAGCTTCTGACTACTTCCCCCATCATTTCTT\nTTACTTCTAGCATCCTCCACAGTAGAAGCTGGAGCAGGAACAGGGTGAACAGTATATCCCCCTCTCGCTG\nGTAACCTAGCCCATGCCGGTGCTTCAGTAGACCTAGCCATCTTCTCCCTCCACTTAGCAGGTGTTTCCTC\nTATCCTAGGTGCTATTAACTTTATTACAACCGCCATCAACATAAAACCCCCAACCCTCTCCCAATACCAA\nACCCCCCTATTCGTATGATCAGTCCTTATTACCGCCGTCCTTCTCCTACTCTCTCTCCCAGTCCTCGCTG");
  }

  @Test(expectedExceptions = NotationException.class)
  public void testReadFastaRNAWithExcepiton() throws FastaFormatException, NotationException, IOException, JDOMException {
    FastaFormat.generateRNAPolymersFromFastaFormatHELM1(">Seq1 [organism=Carpodacus mexicanus] C.mexicanus clone 6b actin (act) mRNA, partial cds\nCCTTTATCTAATCTTTGGAGCATGAGCTGGCATAGTTGGAACCGCCCTCAGCCTCCTCATCCGTGCAG_A\nCTTGGACAACCTGGAACTCTTCTAGGAGACGACCAAATTTACAATGTAATCGTCACTGCCCACGCCTTCG\nTAATAATTTTCTTTATAGTAATACCAATCATGATCGGTGGTTTCGGAAACTGACTAGTCCCACTCATAAT\nCGGCGCCCCCGACATAGCATTCCCCCGTATAAACAACATAAGCTTCTGACTACTTCCCCCATCATTTCTT\nTTACTTCTAGCATCCTCCACAGTAGAAGCTGGAGCAGGAACAGGGTGAACAGTATATCCCCCTCTCGCTG\nGTAACCTAGCCCATGCCGGTGCTTCAGTAGACCTAGCCATCTTCTCCCTCCACTTAGCAGGTGTTTCCTC\nTATCCTAGGTGCTATTAACTTTATTACAACCGCCATCAACATAAAACCCCCAACCCTCTCCCAATACCAA\nACCCCCCTATTCGTATGATCAGTCCTTATTACCGCCGTCCTTCTCCTACTCTCTCTCCCAGTCCTCGCTG");
  }

  @Test
  public void testReadFastaPEPTIDE2() throws FastaFormatException {
    FastaFormat.generatePeptidePolymersFromFASTAFormatHELM1(">seq0\nFQTWEEFSRAAEKLYLADPMKVRVVLKYRHVDGNLCIKVTDDLVCLVYRTDQAQDVKKIEKF\n>seq1\nKYRTWEEFTRAAEKLYQADPMKVRVVLKYRHCDGNLCIKVTDDVVCLLYRTDQAQDVKKIEKFHSQLMRLMELKVTDNKECLKFKTDQAQEAKKMEKLNNIFFTLM\n>seq2\nEEYQTWEEFARAAEKLYLTDPMKVRVVLKYRHCDGNLCMKVTDDAVCLQYKTDQAQDVKKVEKLHGK\n>seq3\nMYQVWEEFSRAVEKLYLTDPMKVRVVLKYRHCDGNLCIKVTDNSVCLQYKTDQAQDVK\n>seq4\nEEFSRAVEKLYLTDPMKVRVVLKYRHCDGNLCIKVTDNSVVSYEMRLFGVQKDNFALEHSLL\n>seq5\nSWEEFAKAAEVLYLEDPMKCRMCTKYRHVDHKLVVKLTDNHTVLKYVTDMAQDVKKIEKLTTLLM\n>seq6\nFTNWEEFAKAAERLHSANPEKCRFVTKYNHTKGELVLKLTDDVVCLQYSTNQLQDVKKLEKLSSTLLRSI\n>seq7\nSWEEFVERSVQLFRGDPNATRYVMKYRHCEGKLVLKVTDDRECLKFKTDQAQDAKKMEKLNNIFF\n>seq8\nSWDEFVDRSVQLFRADPESTRYVMKYRHCDGKLVLKVTDNKECLKFKTDQAQEAKKMEKLNNIFFTLM\n>seq9\nKNWEDFEIAAENMYMANPQNCRYTMKYVHSKGHILLKMSDNVKCVQYRAENMPDLKK\n>seq10\nFDSWDEFVSKSVELFRNHPDTTRYVVKYRHCEGKLVLKVTDNHECLKFKTDQAQDAKKMEK");
  }

  @Test(expectedExceptions = FastaFormatException.class)
  public void testHELMToFastaPeptide() throws ExceptionState, IOException, JDOMException, FastaFormatException, org.helm.notation.NotationException, ChemistryException {
    String notation = "PEPTIDE1{[dY]'3'.I.K}|PEPTIDE2{G.H}$$$$";
    testHELMtoFastaPEPTIDE(notation);
  }

  @Test(expectedExceptions = FastaFormatException.class)
  public void testHELMToFastaPeptideWithException() throws ExceptionState, IOException, JDOMException,
      FastaFormatException, org.helm.notation.NotationException, ChemistryException {
    String notation = "PEPTIDE1{[dY]'3-5'.I.K}|PEPTIDE2{G.H}$$$$";
    testHELMtoFastaPEPTIDE(notation);
  }

  @Test
  public void testHELMToFastaRNA() throws ExceptionState, IOException, JDOMException, FastaFormatException, org.helm.notation.NotationException, ChemistryException {
    String notation = "RNA1{R(U)P.R(T)P.R(G)P.R(C)}$$$$";
    testHELMtoFastaRNA(notation);
  }

  private void testHELMtoFastaPEPTIDE(String notation) throws ExceptionState, IOException, JDOMException,
      FastaFormatException, org.helm.notation.NotationException, ChemistryException {
    ConverterHELM1ToHELM2 converter = new ConverterHELM1ToHELM2();
    String helm2 = converter.doConvert(notation);
    ParserHELM2 parserHELM2 = new ParserHELM2();
    parserHELM2.parse(helm2);

    System.out.println(FastaFormat.generateFastaFromPeptidePolymer(parserHELM2.getHELM2Notation().getListOfPolymers()));
  }

  private void testHELMtoFastaRNA(String notation) throws ExceptionState, IOException, JDOMException,
      FastaFormatException, org.helm.notation.NotationException, ChemistryException {
    ConverterHELM1ToHELM2 converter = new ConverterHELM1ToHELM2();
    String helm2 = converter.doConvert(notation);
    ParserHELM2 parserHELM2 = new ParserHELM2();
    parserHELM2.parse(helm2);

    FastaFormat.generateFastaFromRNAPolymer(parserHELM2.getHELM2Notation().getListOfPolymers());
  }

  @Test
  public void testHELMToAnalogSequenceExamples() throws ExceptionState, IOException, JDOMException,
      FastaFormatException, AnalogSequenceException {
    String notation = "RNA1{[dR](U)P.R(T)P.R(G)P.R(C)}$$$$";
    testHELMAnalogSequence(notation);

    notation = "RNA1{(R(U)P+R(T)P).R(T)P.R(G)P.R(C)}$$$$";
    testHELMAnalogSequence(notation);

    notation = "PEPTIDE1{(A.G).L}$$$$";
    testHELMAnalogSequence(notation);

    notation = "PEPTIDE1{[dF].[dN].[dL]}$$$$";
    testHELMAnalogSequence(notation);
  }

  private void testHELMAnalogSequence(String notation) throws ExceptionState, IOException, JDOMException,
      FastaFormatException, AnalogSequenceException {
    ConverterHELM1ToHELM2 converter = new ConverterHELM1ToHELM2();
    String helm2 = converter.doConvert(notation);
    ParserHELM2 parserHELM2 = new ParserHELM2();
    parserHELM2.parse(helm2);

    FastaFormat.convertIntoAnalogSequence(parserHELM2.getHELM2Notation());
    // System.out.println(containerhelm2.getHELM2Notation().toHELM2());
  }

}
