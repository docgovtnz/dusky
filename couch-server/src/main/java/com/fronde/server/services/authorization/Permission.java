package com.fronde.server.services.authorization;

public enum Permission {

  AUTHENTICATED("Base Permission"),

  PASSWORD_RESET("Reset Password"),

  CHECKSHEET_REPORT_VIEW("View Checksheet Report"),

  MATING_REPORT_VIEW("View Mating Report"),

  LATEST_WEIGHT_REPORT_VIEW("View Latest Weight Report"),

  COUCHBASE_PASSWORD_VIEW("View Couchbase Password"),

  PERSON_VIEW("View Person"),
  PERSON_EDIT("Edit Person"),
  PERSON_DELETE("Delete Person"),

  LOCATION_VIEW("View Location"),
  LOCATION_EDIT("Edit Location"),
  LOCATION_DELETE("Delete Location"),

  BIRD_VIEW("View Bird"),
  BIRD_EDIT("Edit Bird"),
  BIRD_DELETE("Delete Bird"),

  RECORD_VIEW("View Record"),
  RECORD_EDIT("Edit Record"),
  RECORD_DELETE("Delete Record"),

  TRANSMITTER_VIEW("View Transmitter"),
  TRANSMITTER_EDIT("Edit Transmitter"),
  TRANSMITTER_DELETE("Delete Transmitter"),

  FEEDOUT_VIEW("View Feed Out"),
  FEEDOUT_EDIT("Edit Feed Out"),
  FEEDOUT_DELETE("Delete Feed Out"),

  SETTING_VIEW("View Settings"),
  SETTING_EDIT("Edit Settings"),
  SETTING_DELETE("Delete Settings"),

  TXMORTALITY_VIEW("View Tx Mortality"),
  TXMORTALITY_EDIT("Edit Tx Mortality"),
  TXMORTALITY_DELETE("Delete Tx Mortality"),

  LIFESTAGE_VIEW("View Life Stage"),
  LIFESTAGE_EDIT("Edit Life Stage"),
  LIFESTAGE_DELETE("Delete Life Stage"),

  ISLAND_VIEW("View Island"),
  ISLAND_EDIT("Edit Island"),
  ISLAND_DELETE("Delete Island"),

  SNARKACTIVITY_VIEW("View Snark Activity"),
  SNARKACTIVITY_EDIT("Edit Snark Activity"),
  SNARKACTIVITY_DELETE("Delete Snark Activity"),

  SNARKIMPORT_VIEW("View Snark Import"),
  SNARKIMPORT_EDIT("Edit Snark Import"),
  SNARKIMPORT_DELETE("Delete Snark Import"),

  NESTOBSERVATION_VIEW("View Nest Observation"),
  NESTOBSERVATION_EDIT("Edit Nest Observation"),
  NESTOBSERVATION_DELETE("Delete Nest Observation"),

  SAMPLE_VIEW("View Sample"),
  SAMPLE_EDIT("Edit Sample"),
  SAMPLE_DELETE("Delete Sample"),

  NORANET_PROCESS("Process NoraNet Data"),
  NORANET_VIEW("View NoraNet Data"),
  NORANET_EDIT("Edit NoraNet Data"),
  NORANET_DELETE("Delete NoraNet Data"),
  NORANET_ERRORS("View NoraNet Processing Errors");

  Permission(String userText) {
    this.userText = userText;
  }

  private final String userText;

  public String userText() {
    return this.userText;
  }
}