package org.helm.notation2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.helm.notation2.exception.MonomerException;
import org.helm.notation2.tools.SMILES;

/**
 * This class represents a store for monomers. It is mainly used to seperate the
 * monomers coming from a single (XHELM) file from the monomers within the local
 * database.
 *
 * @author maisel
 *
 */
public class MonomerStore {
  private Map<String, Map<String, Monomer>> monomerDB;

  private Map<String, Monomer> smilesMonomerDB;

  /**
   * Constructor with Monomer- and SmilesDB
   *
   * @param monomerDB given monomerDB
   * @param smilesMonomerDB given smiles DB
   */
  public MonomerStore(Map<String, Map<String, Monomer>> monomerDB,
      Map<String, Monomer> smilesMonomerDB) {
    this.monomerDB = monomerDB;
    this.smilesMonomerDB = smilesMonomerDB;
  }

  /**
   *
   * Constructs empty MonomerStore
   *
   */
  public MonomerStore() {
    monomerDB = new TreeMap<String, Map<String, Monomer>>(String.CASE_INSENSITIVE_ORDER);
    smilesMonomerDB = new HashMap<String, Monomer>();
  }

  /**
   * returns MonomerDB
   *
   * @return MonomerDB as {@code Map<String, Map<String, Monomer>>}
   */
  public Map<String, Map<String, Monomer>> getMonomerDB() {
    return monomerDB;
  }

  /**
   * returns SmilesMonomerDB
   *
   * @return SmilesMonomerDB as {@code Map<String, Monomer>}
   */
  public Map<String, Monomer> getSmilesMonomerDB() {
    return smilesMonomerDB;
  }

  /**
   * Adds a monomer to the store
   *
   * @param monomer given monomer
   * @throws IOException if the monomer store can not be saved
   * @throws MonomerException if the monomer is not valid
   */
  public void addMonomer(Monomer monomer) throws IOException,
      MonomerException {
    addMonomer(monomer, false);
  }

  /**
   * Adds a monomer to the store and optionally sets the dbChanged flag
   *
   * @param monomer given monomer
   * @param dbChanged if db was changed
   * @throws IOException if the monomer store can not be saved
   * @throws MonomerException if the monomer is not valid
   */
  public void addMonomer(Monomer monomer, boolean dbChanged)
      throws IOException, MonomerException {
    Map<String, Monomer> monomerMap = monomerDB.get(monomer.getPolymerType());
    String polymerType = monomer.getPolymerType();
    String alternateId = monomer.getAlternateId();
    String smilesString = monomer.getCanSMILES();

    try {
      smilesString = SMILES.getUniqueExtendedSMILES(smilesString);
    } catch (Exception e) {
      smilesString = monomer.getCanSMILES();
    }

    boolean hasSmilesString = (smilesString != null && smilesString.length() > 0);

    if (null == monomerMap) {
      monomerMap = new TreeMap<String, Monomer>(String.CASE_INSENSITIVE_ORDER);
      monomerDB.put(polymerType, monomerMap);
    }

    Monomer copyMonomer = DeepCopy.copy(monomer);

    // ensure the canonical SMILES is indexed in the monomer store
    if (hasSmilesString) {
      copyMonomer.setCanSMILES(smilesString);
    }

    boolean alreadyAdded = false;
    alreadyAdded = monomerMap.containsKey(alternateId);

    if (!alreadyAdded) {
      monomerMap.put(alternateId, copyMonomer);

      boolean alreadyInSMILESMap = hasSmilesString
          && (smilesMonomerDB.containsKey(smilesString));

      if (!alreadyInSMILESMap) {
        smilesMonomerDB.put(smilesString, copyMonomer);
      }
    }

    if (dbChanged) {
      MonomerFactory.setDBChanged(true);
    }
  }

  /**
   * Checks if a specific monomer exists in the store
   *
   * @param polymerType polymer type of monomer
   * @param alternateId alternateId of monomer
   * @return true if monomer exists, false if not
   */
  public boolean hasMonomer(String polymerType, String alternateId) {
    return ((monomerDB.get(polymerType) != null) && getMonomer(polymerType, alternateId) != null);
  }

  /**
   * Returns the monomer specified by polymerType and alternatId
   *
   * @param polymerType polymer type of monomer
   * @param alternateId alternateId of monomer
   * @return the matching monomer
   */
  public Monomer getMonomer(String polymerType, String alternateId) {
	Map<String, Monomer> map1 = monomerDB.get(polymerType);
	//alternateId = alternateId.toUpperCase();
    return monomerDB.get(polymerType).get(alternateId);
  }

  /**
   * Returns the monomer by smiles string
   *
   * @param smiles given smiles
   * @return the matching monomer
   */
  public Monomer getMonomer(String smiles) {
    return smilesMonomerDB.get(smiles);
  }

  /**
   * Returns all monomers by polymerType
   *
   * @param polymerType given polymer type
   * @return All monomers with polymerType
   */
  public Map<String, Monomer> getMonomers(String polymerType) {
    return monomerDB.get(polymerType);
  }

  /**
   * Adds a monomer to the store and makes it a temporary new monomer
   *
   * @param monomer given monomer
   * @throws IOException if the monomer can not be saved
   * @throws MonomerException if the monomer is not valid
   */
  public synchronized void addNewMonomer(Monomer monomer) throws IOException,
      MonomerException {
    monomer.setNewMonomer(true);
    addMonomer(monomer, true);
  }

  /**
   * Checks for the empty store
   *
   * @return true if the store is empty, false if not
   */
  public boolean isMonomerStoreEmpty() {
    return (this.monomerDB == null || this.monomerDB.values() == null || this.monomerDB.values().size() == 0);
  }

  /**
   * Clears the MonomerStore
   */
  public synchronized void clearMonomers() {
    this.monomerDB.clear();
    this.smilesMonomerDB.clear();
  }

  @Override
  public String toString() {
    String str = "";
    for (Map<String, Monomer> val : this.monomerDB.values()) {
      for (Monomer mon : val.values()) {
        str += mon.getAlternateId() + "(" + mon.getPolymerType()
            + "); ";
      }
      str += System.getProperty("line.separator");
    }

    return str;
  }

  /**
   * Returns the polymer type set
   *
   * @return the polymer type set as {@code Set<String>}
   */
  public Set<String> getPolymerTypeSet() {
    return monomerDB.keySet();
  }

  /**
   * This method returns all monomers of the store as list sorted by polymer
   * type
   *
   * @return all monomers of store as {@code List<{@link Monomer}>}
   */
  public List<Monomer> getAllMonomersList() {
    List<Monomer> monomers = new ArrayList<Monomer>();
    for (String polymerType : getPolymerTypeSet()) {
      Map<String, Monomer> map = getMonomers(polymerType);
      monomers.addAll(map.values());
    }
    return monomers;

  }
}
