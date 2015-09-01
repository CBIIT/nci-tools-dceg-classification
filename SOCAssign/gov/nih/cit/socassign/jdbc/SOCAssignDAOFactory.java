package gov.nih.cit.socassign.jdbc;


public class SOCAssignDAOFactory {

	static Class<? extends SOCAssignDAO> implementationClass=SQLiteSOCAssignDAO.class;
	
	public static void setImplementation(Class<? extends SOCAssignDAO> implementationClass){
		SOCAssignDAOFactory.implementationClass=implementationClass;
	}
	public static SOCAssignDAO getDAO(){
		SOCAssignDAO dao;
		try {
			dao = implementationClass.newInstance();
		} catch (Exception e) {
			dao = new SQLiteSOCAssignDAO();
		}
		
		return dao;
	}

}
