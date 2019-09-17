package repository;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public interface InterfaceServices {

	// GET ID DE CONTACTS
	int getContactIdByEmail(String email);

	// POST SAVE CONTACT
	void postContactSave(String person) throws ClientProtocolException, IOException;

	// DELETE CONTACT
	void deleteContact(int id) throws ClientProtocolException, IOException;

	String serializeContact(String person) throws IOException;

	String checkExistenceForDeleteByEmail(String email) throws ClientProtocolException, IOException;

	String checkExistenceForCreateByJson(String contactJson) throws ClientProtocolException, IOException;

}