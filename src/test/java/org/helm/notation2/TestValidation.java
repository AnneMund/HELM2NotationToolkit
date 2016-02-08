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

import org.helm.chemtoolkit.CTKException;
import org.helm.notation.MonomerException;
import org.helm.notation.NotationException;
import org.helm.notation2.exception.AttachmentException;
import org.helm.notation2.exception.ChemistryException;
import org.helm.notation2.exception.HELM2HandledException;
import org.helm.notation2.exception.PolymerIDsException;
import org.helm.notation2.parser.StateMachineParser;
import org.helm.notation2.parser.exceptionparser.ExceptionState;
import org.jdom2.JDOMException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TestValidation
 *
 * @author hecht
 */
public class TestValidation {
  StateMachineParser parser;

  @Test
  public void testValidationGrouping() throws ExceptionState, MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.X.G.C.(_,N).(A:10,G:30,R:30).T.C.F.D.W\"mutation\".(A:?+G:1.5).C}|RNA1{R(A)P.(R(N)P)'4'.(R(G)P)'3-7'\"mutation\"}|CHEM1{?}|BLOB1{BEAD}\"Animated Polystyrene\"$PEPTIDE1,BLOB1,X:R3-?:?\"Specific Conjugation\"|PEPTIDE1,CHEM1,(A+T):R3-?:?|PEPTIDE1,PEPTIDE1,(4,8):pair-12:pair$G1(PEPTIDE1:1+RNA1:2.5-2.7+BLOB1)|G2(G1:45,CHEM1:55)${\"Name\":\"lipid nanoparticle with RNA payload and peptide ligand\"}$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertTrue(Validation.validateGrouping(parser.notationContainer));
  }

  @Test
  public void testValidationGroupingWithException() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.X.G.C.(_,N).(A:10,G:30,R:30).T.C.F.D.W\"mutation\".(A:?+G:1.5).C}|RNA1{R(A)P.(R(N)P)'4'.(R(G)P)'3-7'\"mutation\"}|CHEM1{?}|BLOB1{BEAD}\"Animated Polystyrene\"$PEPTIDE1,BLOB1,X:R3-?:?\"Specific Conjugation\"|PEPTIDE1,CHEM1,(A+T):R3-?:?|PEPTIDE1,PEPTIDE1,(4,8):pair-12:pair$G1(PEPTIDE1:1+RNA1:2.5-2.7+BLOB1)|G2(G1:45,CHEM3:55)${\"Name\":\"lipid nanoparticle with RNA payload and peptide ligand\"}$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertFalse(Validation.validateGrouping(parser.notationContainer));
  }

  @Test
  public void testValidationGroupingFalseGroup() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.X.G.C.(_,N).(A:10,G:30,R:30).T.C.F.D.W\"mutation\".(A:?+G:1.5).C}|RNA1{R(A)P.(R(N)P)'4'.(R(G)P)'3-7'\"mutation\"}|CHEM1{?}|BLOB1{BEAD}\"Animated Polystyrene\"$PEPTIDE1,BLOB1,X:R3-?:?\"Specific Conjugation\"|PEPTIDE1,CHEM1,(A+T):R3-?:?|PEPTIDE1,PEPTIDE1,(4,8):pair-12:pair$G1(PEPTIDE1:1+RNA1:2.5-2.7+BLOB1)|G2(G3:45,CHEM1:55)${\"Name\":\"lipid nanoparticle with RNA payload and peptide ligand\"}$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertFalse(Validation.validateGrouping(parser.notationContainer));
  }

  @Test
  public void testvalidateUniquePolymerIDs() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException, PolymerIDsException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.X.G.C.(_,N).(A:10,G:30,R:30).T.C.F.D.W\"mutation\".(A:?+G:1.5).C}|RNA1{R(A)P.(R(N)P)'4'.(R(G)P)'3-7'\"mutation\"}|CHEM1{?}|BLOB1{BEAD}\"Animated Polystyrene\"$PEPTIDE1,BLOB1,X:R3-?:?\"Specific Conjugation\"|PEPTIDE1,CHEM1,(A+T):R3-?:?|PEPTIDE1,PEPTIDE1,(4,8):pair-12:pair$G1(PEPTIDE1:1+RNA1:2.5-2.7+BLOB1)|G2(G3:45,CHEM1:55)${\"Name\":\"lipid nanoparticle with RNA payload and peptide ligand\"}$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertTrue(Validation.validateUniquePolymerIDs(parser.notationContainer));
  }

  @Test
  public void testvalidateUniquePolymerIDsWithException() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException, PolymerIDsException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.X.G.C.(_,N).(A:10,G:30,R:30).T.C.F.D.W\"mutation\".(A:?+G:1.5).C}|RNA1{R(A)P.(R(N)P)'4'.(R(G)P)'3-7'\"mutation\"}|CHEM1{?}|BLOB1{BEAD}\"Animated Polystyrene\"$PEPTIDE1,BLOB1,X:R3-?:?\"Specific Conjugation\"|PEPTIDE1,CHEM1,(A+T):R3-?:?|PEPTIDE1,PEPTIDE1,(4,8):pair-12:pair$G1(PEPTIDE1:1+RNA1:2.5-2.7+BLOB1)|G1(G3:45,CHEM1:55)${\"Name\":\"lipid nanoparticle with RNA payload and peptide ligand\"}$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertFalse(Validation.validateUniquePolymerIDs(parser.notationContainer));
  }

  @Test
  public void testGetMonomerCountsSimple() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.X.G.C.C}|RNA1{R(A)P.(R(N)P).(R(G)P)}|CHEM1{?}|BLOB1{BEAD}\"Animated Polystyrene\"$PEPTIDE1,BLOB1,X:R3-?:?\"Specific Conjugation\"|PEPTIDE1,CHEM1,(A+T):R3-?:?|PEPTIDE1,PEPTIDE1,(4,8):pair-12:pair$G1(PEPTIDE1:1+RNA1:2.5-2.7+BLOB1)|G2(G3:45,CHEM1:55)${\"Name\":\"lipid nanoparticle with RNA payload and peptide ligand\"}$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertEquals(Validation.getMonomerCountAll(parser.notationContainer), 16);

  }

  @Test
  public void testGetMonomerCountsExtended() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.X.G.C.C.(A.X.C)'4'}|RNA1{R(A)P.(R(N)P).(RP)}|CHEM1{?}|BLOB1{BEAD}\"Animated Polystyrene\"$PEPTIDE1,BLOB1,X:R3-?:?\"Specific Conjugation\"|PEPTIDE1,CHEM1,(A+T):R3-?:?|PEPTIDE1,PEPTIDE1,(4,8):pair-12:pair$G1(PEPTIDE1:1+RNA1:2.5-2.7+BLOB1)|G2(G3:45,CHEM1:55)${\"Name\":\"lipid nanoparticle with RNA payload and peptide ligand\"}$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertEquals(Validation.getMonomerCountAll(parser.notationContainer), 27);
  }

  @Test
  public void testConnectionRNA() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException,
      AttachmentException, PolymerIDsException, HELM2HandledException, CTKException, ChemistryException

  {
    parser = new StateMachineParser();

    String test =
        "RNA1{[sP].R(C)[sP].R(U)P.R(G)P.R([dabA])P.R(G)P.R(A)P.R(G)P.R(G)P.[dR](G)P.R(U)}|RNA2{R(A)P.R(C)P.R(C)P.R(C)P.R(U)P.R(C)P.R(U)P.R(C)P.R(A)P.R(G)}$RNA1,RNA2,9:pair-23:pair|RNA1,RNA2,6:pair-26:pair|RNA1,RNA2,21:pair-11:pair|RNA1,RNA2,15:pair-17:pair|RNA1,RNA2,12:pair-20:pair|RNA1,RNA2,24:pair-8:pair|RNA1,RNA2,30:pair-2:pair|RNA1,RNA2,18:pair-14:pair|RNA1,RNA2,27:pair-5:pair|RNA1,RNA2,3:pair-29:pair$$$";

    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertTrue(Validation.validateConnections(parser.notationContainer));

  }

  @Test
  public void testConnection() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException,
      AttachmentException, PolymerIDsException, HELM2HandledException, CTKException, ChemistryException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{F.L.C'3'}|PEPTIDE2{C.D}$PEPTIDE2,PEPTIDE1,1:R3-4:R3$$$";

    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertTrue(Validation.validateConnections(parser.notationContainer));

  }

  @Test
  public void testConnectionMap() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException,
      AttachmentException, PolymerIDsException, HELM2HandledException, CTKException, ChemistryException

  {
    parser = new StateMachineParser();

    String test =
        "RNA1{R(U)P.R(T)P.R(G)P.R(C)P.R(A)}$$$$";

    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";
    parser.notationContainer.getListOfPolymers().get(0).initializeMapOfMonomersAndMapOfIntraConnection();

    Assert.assertTrue(Validation.validateConnections(parser.notationContainer));

  }

  @Test
  public void testConnectionFalse() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException,
      AttachmentException, PolymerIDsException, HELM2HandledException, CTKException, ChemistryException

  {
    parser = new StateMachineParser();

    String test =
        "RNA1{R(U)P.R(T)P.R(G)P.R(C)P.R(A)}|RNA2{R(U)P.R(G)P.R(C)P.R(A)P.R(A)}$RNA1,RNA2,14:pair-2:pair|RNA1,RNA2,11:pair-5:pair|RNA1,RNA2,2:pair-14:pair|RNA1,RNA2,8:pair-14:pair|RNA1,RNA2,5:pair-11:pair$$$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertFalse(Validation.validateConnections(parser.notationContainer));

  }

  @Test
  public void testConnectionHELM2Simple() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException,
      AttachmentException, PolymerIDsException, HELM2HandledException, CTKException, ChemistryException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.C.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E}|PEPTIDE2{G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.C.S.S.S.S.S.S.S.S.S.P.P.P.P.P.P.P.P.P.K.K.K.K.K.K.K.K.K.K.K.K.K}|CHEM1{[SMPEG2]}$PEPTIDE1,CHEM1,C:R3-1:R1\"Specific Conjugation\"$$$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertTrue(Validation.validateConnections(parser.notationContainer));

  }

  @Test
  public void testConnectionHELM2SimpleWithException() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException,
      AttachmentException, PolymerIDsException, HELM2HandledException, CTKException, ChemistryException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.C.C.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E}|PEPTIDE2{G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.C.S.S.S.S.S.S.S.S.S.P.P.P.P.P.P.P.P.P.K.K.K.K.K.K.K.K.K.K.K.K.K}|CHEM1{[SMPEG2]}$PEPTIDE1,CHEM1,G:R3-1:R1\"Specific Conjugation\"$$$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertFalse(Validation.validateConnections(parser.notationContainer));

  }

  @Test
  public void testConnectionHELM2SimpleWithExceptio() throws ExceptionState,
      MonomerException,
      IOException, NotationException, JDOMException, org.jdom2.JDOMException,
      AttachmentException, PolymerIDsException, HELM2HandledException, CTKException, ChemistryException

  {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.C.C.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E}|PEPTIDE2{G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.C.S.S.S.S.S.S.S.S.S.P.P.P.P.P.P.P.P.P.K.K.K.K.K.K.K.K.K.K.K.K.K}|CHEM1{[SMPEG2]}$PEPTIDE1,CHEM1,C:R3-1:R1\"Specific Conjugation\"$$$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertTrue(Validation.validateConnections(parser.notationContainer));

  }

  @Test
  public void testConnectionHELM2Extended() throws ExceptionState,
      MonomerException, IOException, NotationException, JDOMException, org.jdom2.JDOMException, AttachmentException,
      PolymerIDsException, HELM2HandledException, CTKException, ChemistryException {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.C.C.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E}|PEPTIDE2{C.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.C.S.S.S.S.S.S.S.S.S.P.P.P.P.P.P.P.P.P.K.K.K.K.K.K.K.K.K.K.K.K.K}|CHEM1{[SMPEG2]}$PEPTIDE1,PEPTIDE2,(C,D):R3-1:R3\"Specific Conjugation\"$$$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertTrue(Validation.validateConnections(parser.notationContainer));

  }

  @Test
  public void testConnectionHELM2ExtendedWithException() throws ExceptionState,
      MonomerException, IOException, NotationException, JDOMException, org.jdom2.JDOMException, AttachmentException,
      PolymerIDsException, HELM2HandledException, CTKException, ChemistryException {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.C.C.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E}|PEPTIDE2{G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.C.S.S.S.S.S.S.S.S.S.P.P.P.P.P.P.P.P.P.K.K.K.K.K.K.K.K.K.K.K.K.K}|CHEM1{[SMPEG2]}$PEPTIDE1,CHEM1,(C,P):?-1:?\"Specific Conjugation\"$$$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertFalse(Validation.validateConnections(parser.notationContainer));

  }

  @Test
  public void testConnectionHELM2Extended2() throws ExceptionState,
      MonomerException, IOException, NotationException, JDOMException, org.jdom2.JDOMException, AttachmentException,
      PolymerIDsException, HELM2HandledException, CTKException, ChemistryException {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.A.C.C.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.D.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E.E}|PEPTIDE2{G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.G.C.S.S.S.S.S.S.S.S.S.P.P.P.P.P.P.P.P.P.K.K.K.K.K.K.K.K.K.K.K.K.K}|CHEM1{[SMPEG2]}$PEPTIDE1,CHEM1,(C,D):?-1:?\"Specific Conjugation\"$$$";
    ;
    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }
    test += "V2.0";

    Assert.assertTrue(Validation.validateConnections(parser.notationContainer));

  }

  @Test
  public void testMonomerValidation() throws ExceptionState, IOException, JDOMException, MonomerException,
      org.jdom2.JDOMException, NotationException, CTKException, ChemistryException {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.X.G.C.(_,N).(A:10,G:30,R:30)'4'\"Group is repeated\".T.C.F.D.W\"mutation\".(A:?+G:1.5).C}|RNA1{[am6]P.(R(N)P)'4'.(R(G)P)'3-7'\"mutation\"}|CHEM1{?}|BLOB1{BEAD}\"AnimatedPolystyrene\"$PEPTIDE1,BLOB1,X:R3-?:?\"Specific Conjugation\"|PEPTIDE1,CHEM1,(A+T):R3-?:?|PEPTIDE1,PEPTIDE1,(4,8):pair-12:pair$G1(PEPTIDE1:1+RNA1:2.5-2.7+BLOB1)|G2(G1:45,CHEM1:55)${\"Name\":\"lipidnanoparticle with RNA payload and peptide ligand\"}$";
    ;

    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }

    Assert.assertTrue(Validation.validateMonomers(MethodsForContainerHELM2.getListOfMonomerNotation(parser.notationContainer.getListOfPolymers())));

  }

  @Test
  public void testMonomerValidationWithException() throws ExceptionState, IOException, JDOMException, MonomerException,
      org.jdom2.JDOMException, NotationException, CTKException, ChemistryException {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{Z.X.G.C.(_,N).(A:10,G:30,R:30)'4'\"Group is repeated\".T.C.F.D.W\"mutation\".(A:?+G:1.5).C}|RNA1{[am6]P.(R(N)P)'4'.(R(G)P)'3-7'\"mutation\"}|CHEM1{?}|BLOB1{BEAD}\"AnimatedPolystyrene\"$PEPTIDE1,BLOB1,X:R3-?:?\"Specific Conjugation\"|PEPTIDE1,CHEM1,(A+T):R3-?:?|PEPTIDE1,PEPTIDE1,(4,8):pair-12:pair$G1(PEPTIDE1:1+RNA1:2.5-2.7+BLOB1)|G2(G1:45,CHEM1:55)${\"Name\":\"lipidnanoparticle with RNA payload and peptide ligand\"}$";
    ;

    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }

    Assert.assertFalse(Validation.validateMonomers(MethodsForContainerHELM2.getListOfMonomerNotation(parser.notationContainer.getListOfPolymers())));

  }

  @Test
  public void testMonomerValidationWithException2() throws ExceptionState, IOException, JDOMException,
      MonomerException, org.jdom2.JDOMException, NotationException, CTKException, ChemistryException {
    parser = new StateMachineParser();

    String test =
        "PEPTIDE1{A.X.G.C.(_,N).(A:10,Z:30,R:30)'4'\"Group is repeated\".T.C.F.D.W\"mutation\".(A:?+G:1.5).C}|RNA1{[am6]P.(R(N)P)'4'.(R(G)P)'3-7'\"mutation\"}|CHEM1{?}|BLOB1{BEAD}\"AnimatedPolystyrene\"$PEPTIDE1,BLOB1,X:R3-?:?\"Specific Conjugation\"|PEPTIDE1,CHEM1,(A+T):R3-?:?|PEPTIDE1,PEPTIDE1,(4,8):pair-12:pair$G1(PEPTIDE1:1+RNA1:2.5-2.7+BLOB1)|G2(G1:45,CHEM1:55)${\"Name\":\"lipidnanoparticle with RNA payload and peptide ligand\"}$";
    ;

    for (int i = 0; i < test.length(); ++i) {
      parser.doAction(test.charAt(i));
    }

    Assert.assertFalse(Validation.validateMonomers(MethodsForContainerHELM2.getListOfMonomerNotation(parser.notationContainer.getListOfPolymers())));

  }

}
