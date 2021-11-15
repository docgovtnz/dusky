package com.fronde.server.services.authorization;

/**
 *
 */
public class RoleTypes {

  public static final String System_Admin = "System Admin";
  public static final String Power_User = "Power User";
  public static final String Standard_User = "Standard User";
  public static final String Data_Entry_User = "Data Entry User";
  public static final String Data_Replication = "Data Replication";
  public static final String Report_Runner = "Report Runner";
  public static final String Public_User = "Public User";
  public static final String Ad_hoc_Reporter = "Ad-hoc Reporter";
  public static final String AWS_NoraNet_User = "AWS NoraNet User";


  public static final String[] ROLE_LIST = new String[]{
      System_Admin,
      Power_User,
      Standard_User,
      Data_Entry_User,
      Data_Replication,
      Report_Runner,
      Public_User,
      Ad_hoc_Reporter,
      AWS_NoraNet_User
  };

}
