CREATE TABLE EN_DOC_HDR_EXT_LONG_T (
	DOC_HDR_EXT_ID			NUMBER(19) NOT NULL,
	DOC_HDR_ID				NUMBER(14) NOT NULL,
	DOC_HDR_EXT_VAL_KEY		VARCHAR2(256) NOT NULL,
	DOC_HDR_EXT_VAL			NUMBER(22) NULL,
	CONSTRAINT EN_DOC_HDR_EXT_LONG_T_PK PRIMARY KEY (DOC_HDR_EXT_ID) USING INDEX
) 
/