CREATE DATABASE servicelocator;

-- For Dev
CREATE USER '_serlocatoruser' identified by '__servicelocatordev123__';

-- GRANT SELECT, DELETE, UPDATE, INSERT on servicelocator.* to _servicelocatoruser@'app1-test.pensoft.local' identified by '__servicelocatordev123__';
-- GRANT SELECT, DELETE, UPDATE, INSERT on servicelocator.* to _servicelocatoruser@'app2-test.pensoft.local' identified by '__servicelocatordev123__';
GRANT SELECT, DELETE, UPDATE, INSERT on servicelocator.* to _serlocatoruser@'app3-test.pensoft.local' identified by '__servicelocatordev123__';

-- For QA
CREATE USER '_serlocatoruser' identified by '__servicelocatorqa123__';

GRANT SELECT, DELETE, UPDATE, INSERT on servicelocator.* to _serlocatoruser@'app1-qa.pensoft.local' identified by '__servicelocatorqa123__';
GRANT SELECT, DELETE, UPDATE, INSERT on servicelocator.* to _serlocatoruser@'app2-qa.pensoft.local' identified by '__servicelocatorqa123__';

-- For Prod
CREATE USER '_serlocatoruser' identified by '__slg834__';
GRANT SELECT, DELETE, UPDATE, INSERT on servicelocator.* to _serlocatoruser@'app1' identified by '__slg834__';
GRANT SELECT, DELETE, UPDATE, INSERT on servicelocator.* to _serlocatoruser@'app2' identified by '__slg834__';

-- For local 
CREATE USER '_serlocatoruser' identified by '__servicelocatordev123__';
GRANT SELECT, DELETE, UPDATE, INSERT on servicelocator.* to _serlocatoruser@'localhost' identified by '__servicelocatordev123__';

-- For Stage
CREATE USER '_serlocatoruser' identified by '__slg834__';
GRANT SELECT, DELETE, UPDATE, INSERT on servicelocator.* to _serlocatoruser@'app1-stage.pensoft.local' identified by '__slg834__';
GRANT SELECT, DELETE, UPDATE, INSERT on servicelocator.* to _serlocatoruser@'app2-stage.pensoft.local' identified by '__slg834__';


-- In database servicelocator create these tables
CREATE TABLE ServiceLocators (
	id INT NOT NULL AUTO_INCREMENT,
    version TINYINT NOT NULL DEFAULT 0,
    dateCreated TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
    dateUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    serviceName CHAR(30) NOT NULL,
    domainName CHAR(30) DEFAULT 'LIVESCRIBE',
    secure BOOLEAN DEFAULT true,
    uriSuffix TEXT,
    hostKey CHAR(30) DEFAULT 'WWW',
    protocol CHAR(30) DEFAULT 'XML-RPC',
    active BOOLEAN DEFAULT true,
    PRIMARY KEY (id)
);

CREATE INDEX ServiceLocators_Name_Idx on ServiceLocators(serviceName);
CREATE INDEX ServiceLocators_Domain_Idx on ServiceLocators(domainName);

DELIMITER $$ 
CREATE trigger trgrServiceLocatorCreateTime 
before insert on ServiceLocators 
for each row 
begin 
 set new.dateCreated = current_timestamp; 
end$$ 
DELIMITER ; 


-- Data
INSERT INTO ServiceLocators ( serviceName, uriSuffix, active, hostKey ) VALUES ('LSAwsAuthorizationService', '/services/lsawsauthorization/xmlrpc', 1, 'WWW');
INSERT INTO ServiceLocators ( serviceName, uriSuffix, active, hostKey ) VALUES ('NagiosMonitorEntry', '/okay', 1, 'WWW');

-- For DEV only
INSERT INTO ServiceLocators ( serviceName, uriSuffix ) VALUES ('LSDrmServer', 'http://app3-test.pensoft.local:8280/LSDrmServer/xmlrpc/');

