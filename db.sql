CREATE DATABASE DevTools;
USE DevTools;

CREATE TABLE User(
	uid VARCHAR(20) NOT NULL,
	pwd VARCHAR(64) NOT NULL,
	sex INTEGER(1),
	name VARCHAR(10) NOT NULL,
	auth INTEGER(1),
	PRIMARY KEY (uid)
);

CREATE TABLE Template(
	name VARCHAR(30) NOT NULL,
	createdBy VARCHAR(20),
	PRIMARY KEY (name),
	FOREIGN KEY (createdBy) REFERENCES User(uid)
);


CREATE TABLE Dev(
	id INTEGER AUTO_INCREMENT NOT NULL,
	name TEXT,
	version TEXT,
	insPath TEXT,
	details TEXT,
	reference TEXT,
	uid VARCHAR(20),
	template VARCHAR(30),
	PRIMARY KEY (id),
	FOREIGN KEY (uid) REFERENCES User(uid),
	FOREIGN KEY (template) REFERENCES Template(name)
);
