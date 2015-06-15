package gov.nih.cit.socassign.codingsysten;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * <P>Represents a Coding System.  The coding systems we use are hierarchical, we use a tree to model the 
 * system.  The Coding System class is the root of the tree.  It also contains methods useful to quickly
 * lookup codes in the tree.</P>
 * 
 * <P>The coding systems are stored in XML format.  The static factory methods loadSystem() should be used to instantiate
 * a coding system.  The AssignmentCodingSystem enum holds all the known coding system and ensures that only one instance of
 * each system is created.</P>
 * 
 * <P>The format of the XML file is:</P>
 * <pre>
 * <table>
 * <tr>
 * <td>
 * {@code
 * <SystemName>
 *     <level1name name=code title=def>
 *     	   <level2name name=code title=def>
 *             ...
 *         </level2name>
 *         ...
 *     </level1name>
 *     ...
 * </SystemName>
 * }
 * </td>
 * <td>
 * {@code
 * <SOC2010>
 *      <major name="11-0000" title="Management Occupations">
 *          <minor name="11-1000" title="Top Executives">
 *              <broad name="11-1010" title="Chief Executives">
 *                  <detailed name="11-1011" title="Chief Executives"/>
 *                     ...
 *              </broad>
 *              ...
 *          </minor>
 *          ....
 *      </major>
 *      ....
 * </SOC2010>
 * }
 * </td>
 * </tr>
 * </table>
 * </pre>
 * @author Daniel Russ
 *
 */
public class CodingSystem extends OccupationCode{

	private static DocumentBuilder builder;
	static{
		try {
			builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private final Map<String, OccupationCode> codeLookup;
	private final Map<String, List<OccupationCode>> levelMap;

	private CodingSystem(String systemName, Map<String, OccupationCode> codeLookup, Map<String, List<OccupationCode>> levelMap) {
		super(null, systemName, null, "root");
		this.codeLookup=codeLookup;
		this.levelMap=levelMap;
	}

	public OccupationCode getRoot() {
		return this;
	}

	public String lookup(String code){
		if (codeLookup.get(code)==null) return "Not a valid code";
		return codeLookup.get(code).getTitle();
	}

	public OccupationCode getOccupationalCode(String code){
		return codeLookup.get(code);
	}
	
	public List<OccupationCode> getListofCodes(){
		ArrayList<OccupationCode> codeList=new ArrayList<OccupationCode>(codeLookup.values());
		Collections.sort(codeList, codeComparator);
		return codeList;
	}
	
	public List<OccupationCode> getListOfCodesAtLevel(String level){
		List<OccupationCode> codes=levelMap.get(level);
		if (codes==null) codes=new ArrayList<OccupationCode>();
		return codes;
	}
	public List<String> getListOfCodesAsStrings(){
		List<String> codes=new ArrayList<String>(codeLookup.keySet());
		Collections.sort(codes);
		return codes;
	}
	
	public int getIndexOfCodeAtLevel(OccupationCode code){
		List<OccupationCode> list=levelMap.get(code.getLevel());
		return list.indexOf(code);
	}

	
	@Override
	public CodingSystem getCodingSystem() {
		return this;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Builder Method for creating a Coding System...
	 * @param xmlFilename the filename of the coding system in XML format
	 * @return the coding system
	 * @throws IOException -- An exception occurred loading or parsing the xml.
	 */
	public static CodingSystem loadSystem(String xmlFilename) throws IOException{
		return loadSystem(CodingSystem.class.getResourceAsStream(xmlFilename));
	}

	/** 
	 * Builder Method for creating a CodingSystem.
	 * @param is an inputstream for the XML source of the coding system
	 * @return the coding system
	 * @throws IOException An exception occurred loading or parsing the xml.
	 */
	public static CodingSystem loadSystem(InputStream is) throws IOException{
		Document xmlDocument=null;
		try {
			xmlDocument = builder.parse(is);
		} catch (SAXException e) {
			throw new IOException(e);
		}
		
		Map<String, OccupationCode> codeMap=new HashMap<String, OccupationCode>();
		Map<String, List<OccupationCode>> levelMap=new HashMap<String, List<OccupationCode>>();
		
		Element documentElement=xmlDocument.getDocumentElement();
		String systemName=documentElement.getNodeName();

		// Build the CodingSytstem (AKA the root of the tree)
		CodingSystem codingSystem=new CodingSystem(systemName, codeMap, levelMap);
		addCodeToMaps(codingSystem, codeMap, levelMap);		
		addChildren(codingSystem, documentElement, codeMap, levelMap);
		
		return codingSystem;
	}

	private static void addChildren(OccupationCode code, Element xmlElement,
			Map<String, OccupationCode> codeMap,Map<String,List<OccupationCode>> levelMap){
		
		// for each child of the parent create the kids...
		NodeList myChildren=xmlElement.getChildNodes();
		
		for (int i=0;i<myChildren.getLength();i++){
			if (myChildren.item(i).getNodeType()==Node.ELEMENT_NODE){
				Element myChildsElement=(Element)myChildren.item(i);
				// create one of my children and add it to my family...
				OccupationCode myChild=null;
				if (myChildsElement.hasAttribute("description")){
					myChild=new OccupationCode(code,myChildsElement.getAttribute("name"), myChildsElement.getAttribute("title"),myChildsElement.getAttribute("description"),myChildsElement.getNodeName());
				}else{
					myChild=new OccupationCode(code,myChildsElement.getAttribute("name"), myChildsElement.getAttribute("title"),myChildsElement.getNodeName());
				}
				
				code.addChild(myChild);
				addCodeToMaps(myChild, codeMap, levelMap);
				
				// tell my child to add all its children
				addChildren(myChild, myChildsElement, codeMap, levelMap);
			}
		}
	}
	
	private static void addCodeToMaps(OccupationCode code,Map<String, OccupationCode> codeMap,Map<String,List<OccupationCode>> levelMap){
		codeMap.put(code.getName(), code);
		if (!levelMap.containsKey(code.getLevel())){
			levelMap.put(code.getLevel(), new ArrayList<OccupationCode>());
		}
		levelMap.get(code.getLevel()).add(code);
	}
	

	
	private static Comparator<OccupationCode> codeComparator=new Comparator<OccupationCode>() {
		@Override
		public int compare(OccupationCode code1, OccupationCode code2) {
			
			return code1.getName().compareTo(code2.getName());
		}
	};
}
