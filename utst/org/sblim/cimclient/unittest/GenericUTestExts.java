/**
 * (C) Copyright IBM Corp. 2009
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Ramandeep Arora, arorar@us.ibm.com  
 * 
 * Flag       Date        Prog         Description
 * ------------------------------------------------------------------------
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2) 		
 */

package org.sblim.cimclient.unittest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Class GenericExts is responsible for generic initialization
 */
public class GenericUTestExts {

	/**
	 * initArrayList : If arrayList is null then it will return the new
	 * arrayList of same type if it is not null then it will clear the arrayList
	 * 
	 * @param <T>
	 * 
	 * @param pAL
	 * @return ArrayList
	 */
	public static <T> ArrayList<T> initClearArrayList(ArrayList<T> pAL) {
		if (pAL == null) return new ArrayList<T>();
		pAL.clear();
		return pAL;
	}

	/**
	 * initArrayList : If arrayList is null then it will return the new
	 * arrayList of same type if it is not null then it will return the same
	 * arrayList
	 * 
	 * @param <T>
	 * 
	 * @param pAL
	 * @return ArrayList
	 */
	public static <T> ArrayList<T> initArrayList(ArrayList<T> pAL) {
		if (pAL == null) return new ArrayList<T>();
		return pAL;
	}

	/**
	 * mkList : Creates an ArrayList from array of objects
	 * 
	 * @param <T>
	 * 
	 * @param pObjs
	 *            : An array of objects as of type mentioned in parameter
	 * @return An Arraylist containing pObjs of type as mentioned in parameter.
	 *         Null is returned if pObjs is null
	 */
	public static <T> ArrayList<T> mkList(T[] pObjs) {
		if (pObjs == null) return null;
		return new ArrayList<T>(Arrays.asList(pObjs));
	}

	/**
	 * mkVec : Creates a Vector from array of objects
	 * 
	 * @param <T>
	 * 
	 * @param pObjs
	 *            : An array of objects as of type mentioned in parameter
	 * 
	 * @return : A Vector containing pObjs of type as mentioned in parameter.
	 *         Null is returned if pObjs is null
	 * 
	 */
	public static <T> Vector<T> mkVec(T[] pObjs) {
		if (pObjs == null) return null;
		return new Vector<T>(Arrays.asList(pObjs));
	}

}
