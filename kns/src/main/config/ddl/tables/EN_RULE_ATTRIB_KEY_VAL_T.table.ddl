CREATE TABLE EN_RULE_ATTRIB_KEY_VAL_T (
	RULE_ATTRIB_KEY_VAL_ID NUMBER(19) NOT NULL,
	RULE_ATTRIB_ID NUMBER(19) NOT NULL,
	RULE_ATTRIB_KEY VARCHAR2(2000) NOT NULL,
	DB_LOCK_VER_NBR NUMBER(8) DEFAULT 0,
	CONSTRAINT EN_RULE_ATTRIB_KEY_VAL_T PRIMARY KEY (RULE_ATTRIB_KEY_VAL_ID) USING INDEX
)
/