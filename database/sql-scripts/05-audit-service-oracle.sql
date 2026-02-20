-- Oracle Database Initialization Script for Audit Service
-- Database: Audit Database (Free/XE)

-- 1. Set container to FREEPDB1 (for Oracle Free/XE)
ALTER SESSION SET CONTAINER = FREEPDB1;

-- 2. Grant permissions to audit_user
GRANT CONNECT, RESOURCE, DBA TO audit_user;

-- 3. Grant unlimited tablespace quota
ALTER USER audit_user QUOTA UNLIMITED ON USERS;

-- 4. Grant object creation privileges
GRANT CREATE TABLE, CREATE VIEW, CREATE SEQUENCE TO audit_user;

-- 5. Create Sequence
CREATE SEQUENCE AUDIT_LOG_SEQ 
  START WITH 1 
  INCREMENT BY 1 
  NOCACHE 
  NOCYCLE;

-- 6. Create audit_logs table
CREATE TABLE audit_logs (
    id NUMBER PRIMARY KEY,
    action_type VARCHAR2(20) NOT NULL,
    source_service VARCHAR2(50),
    entity_name VARCHAR2(50) NOT NULL,
    entity_id VARCHAR2(50),
    old_value CLOB,
    new_value CLOB,
    user_id VARCHAR2(50),
    ip_address VARCHAR2(45),
    user_agent VARCHAR2(500),
    created_at TIMESTAMP DEFAULT SYSDATE NOT NULL
);

-- 7. Create indexes for performance
CREATE INDEX idx_audit_logs_entity 
  ON audit_logs(entity_name, entity_id);
  
CREATE INDEX idx_audit_logs_user 
  ON audit_logs(user_id);
  
CREATE INDEX idx_audit_logs_created_at 
  ON audit_logs(created_at);

-- 8. Verification
SELECT 'Audit Service Oracle Database Initialized Successfully!' as status FROM dual;
SELECT table_name FROM user_tables ORDER BY table_name;
