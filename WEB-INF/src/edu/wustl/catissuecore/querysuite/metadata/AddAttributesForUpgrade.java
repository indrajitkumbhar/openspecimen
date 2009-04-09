package edu.wustl.catissuecore.querysuite.metadata;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.wustl.catissuecore.util.global.Constants;


public class AddAttributesForUpgrade extends BaseMetadata
{
	private Connection connection = null;
	private Statement stmt = null;

	public void addAttribute() throws SQLException, IOException
	{
		Statement stmt = connection.createStatement();
		Set<String> keySet = entityNameAttributeNameMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext())
		{
			String entityName = (String) iterator.next();
			List<String> attributes = entityNameAttributeNameMap
			.get(entityName);
			for (String attr : attributes)
			{
				stmt = connection.createStatement();
				String sql = "select max(identifier) from dyextn_abstract_metadata";
				ResultSet rs = stmt.executeQuery(sql);
				int nextIdOfAbstractMetadata = 0;
				if (rs.next())
				{
					int maxId = rs.getInt(1);
					nextIdOfAbstractMetadata = maxId + 1;
				}

				int nextIdAttrTypeInfo = 0;
				sql = "select max(identifier) from dyextn_attribute_type_info";
				rs = stmt.executeQuery(sql);
				if (rs.next())
				{
					int maxId = rs.getInt(1);
					nextIdAttrTypeInfo = maxId + 1;
				}

				int nextIdDatabaseproperties = 0;
				sql = "select max(identifier) from dyextn_database_properties";
				rs = stmt.executeQuery(sql);
				if (rs.next())
				{
					int maxId = rs.getInt(1);
					nextIdDatabaseproperties = maxId + 1;
				}

				sql = "INSERT INTO dyextn_abstract_metadata "
					+ "(IDENTIFIER,CREATED_DATE,DESCRIPTION,LAST_UPDATED,NAME,PUBLIC_ID) values("
					+ nextIdOfAbstractMetadata + ",NULL,NULL,NULL,'" + attr
					+ "',null)";
				if(Constants.MSSQLSERVER_DATABASE.equalsIgnoreCase(UpdateMetadata.DATABASE_TYPE))
				{
					sql = UpdateMetadataUtil.getIndentityInsertStmtForMsSqlServer(sql,"dyextn_abstract_metadata");
				}
				UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
				sql = "INSERT INTO DYEXTN_BASE_ABSTRACT_ATTRIBUTE (IDENTIFIER) values("+ nextIdOfAbstractMetadata + ")";
				UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());

				int entityId = UpdateMetadataUtil.getEntityIdByName(entityName, connection.createStatement());
				sql = "INSERT INTO dyextn_attribute values ("
					+ nextIdOfAbstractMetadata + "," + entityId + ")";
				UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
				String primaryKey = attributePrimarkeyMap.get(attr);
				sql = "insert into dyextn_primitive_attribute (IDENTIFIER,IS_IDENTIFIED,IS_PRIMARY_KEY,IS_NULLABLE)"
					+ " values ("
					+ nextIdOfAbstractMetadata
					+ ",NULL,"+primaryKey+",1)";
				UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());

				sql = "insert into dyextn_attribute_type_info (IDENTIFIER,PRIMITIVE_ATTRIBUTE_ID) values ("
					+ nextIdAttrTypeInfo
					+ ","
					+ nextIdOfAbstractMetadata
					+ ")";
				if(Constants.MSSQLSERVER_DATABASE.equalsIgnoreCase(UpdateMetadata.DATABASE_TYPE))
				{
					sql = UpdateMetadataUtil.getIndentityInsertStmtForMsSqlServer(sql,"dyextn_attribute_type_info");
				}
				UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());

				String dataType = getDataTypeOfAttribute(attr,attributeDatatypeMap);

				if (!dataType.equalsIgnoreCase("String") && !dataType.equalsIgnoreCase("date"))
				{
					sql = "insert into dyextn_numeric_type_info (IDENTIFIER,MEASUREMENT_UNITS,DECIMAL_PLACES,NO_DIGITS) values ("
						+ nextIdAttrTypeInfo + ",NULL,0,NULL)";
					UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
				}

				if (dataType.equalsIgnoreCase("string"))
				{
					sql = "insert into dyextn_string_type_info (IDENTIFIER) values ("
						+ nextIdAttrTypeInfo + ")";
				}
				else if (dataType.equalsIgnoreCase("double"))
				{
					sql = "insert into dyextn_double_type_info (IDENTIFIER) values ("
						+ nextIdAttrTypeInfo + ")";
				}
				else if (dataType.equalsIgnoreCase("int"))
				{
					sql = "insert into dyextn_integer_type_info (IDENTIFIER) values ("
						+ nextIdAttrTypeInfo + ")";
				}
				else if (dataType.equalsIgnoreCase("long"))
				{
					sql = "insert into dyextn_long_type_info (IDENTIFIER) values ("
						+ nextIdAttrTypeInfo + ")";
				}
				else if (dataType.equalsIgnoreCase("date"))
				{
					sql = "insert into dyextn_date_type_info (IDENTIFIER,FORMAT) values ("
						+ nextIdAttrTypeInfo + ",'MM-dd-yyyy')";
				}
				UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());

				String columnName = getColumnNameOfAttribue(attr,attributeColumnNameMap);
				sql = "insert into dyextn_database_properties (IDENTIFIER,NAME) values ("
					+ nextIdDatabaseproperties + ",'" + columnName + "')";
				if(Constants.MSSQLSERVER_DATABASE.equalsIgnoreCase(UpdateMetadata.DATABASE_TYPE))
				{
					sql = UpdateMetadataUtil.getIndentityInsertStmtForMsSqlServer(sql,"dyextn_database_properties");
				}
				UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());

				sql = "insert into dyextn_column_properties (IDENTIFIER,PRIMITIVE_ATTRIBUTE_ID) values ("
					+ nextIdDatabaseproperties
					+ ","
					+ nextIdOfAbstractMetadata + ")";
				UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
			}
		}
	}

	private String getColumnNameOfAttribue(String attr, HashMap<String, String> attributeColumnNameMap)
	{
		return attributeColumnNameMap.get(attr);
	}

	private String getDataTypeOfAttribute(String attr, HashMap<String, String> attributeDatatypeMap)
	{
		return attributeDatatypeMap.get(attr);
	}

	private void populateEntityAttributeMap()
	{
		List<String> attributes = new ArrayList<String>();
		attributes = new ArrayList<String>();
		attributes.add("barcode");
		entityNameAttributeNameMap.put("edu.wustl.catissuecore.domain.CollectionProtocolRegistration",attributes);

		attributes = new ArrayList<String>();
		attributes.add("barcode");
		entityNameAttributeNameMap.put("edu.wustl.catissuecore.domain.SpecimenCollectionGroup",attributes);
	}

	private void populateAttributeColumnNameMap()
	{
		attributeColumnNameMap.put("barcode", "BARCODE");
	}

	private void populateAttributeDatatypeMap()
	{
		attributeDatatypeMap.put("barcode", "string");
	}
	private void populateAttributePrimaryKeyMap()
	{
		attributePrimarkeyMap.put("barcode", "0");
	}
	private void populateEntityList()
	{
		entityList.add("edu.wustl.catissuecore.domain.CollectionProtocolRegistration");
		entityList.add("edu.wustl.catissuecore.domain.SpecimenCollectionGroup");
	}

	public AddAttributesForUpgrade(Connection connection) throws SQLException
	{
		this.connection = connection;
		this.stmt = connection.createStatement();

		populateEntityList();
		populateEntityAttributeMap();
		populateAttributeColumnNameMap();
		populateAttributeDatatypeMap();
		populateAttributePrimaryKeyMap();
	}

	public AddAttributesForUpgrade(Connection connection,
			HashMap<String, List<String>> entityNameAttributeNameMap,
			HashMap<String, String> attributeColumnNameMap,
			HashMap<String, String> attributeDatatypeMap,
			HashMap<String, String> attributePrimarkeyMap,
			List<String> entityList)
	{
		this.connection = connection;
		this.entityNameAttributeNameMap = entityNameAttributeNameMap;
		this.attributeColumnNameMap = attributeColumnNameMap;
		this.attributeDatatypeMap = attributeDatatypeMap;
		this.attributePrimarkeyMap = attributePrimarkeyMap;
		this.entityList = entityList;
	}
}
