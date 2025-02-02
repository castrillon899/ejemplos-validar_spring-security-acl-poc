create table IF NOT EXISTS editor_change
(
    id              integer not null,
    web_property_id integer(255),
    change_content  varchar(255),
    primary key (id)
);

create table IF NOT EXISTS web_property
(
    id   integer not null,
    name varchar(255),
    primary key (id)
);

ALTER TABLE editor_change
    ADD FOREIGN KEY (web_property_id) REFERENCES web_property (id);

CREATE TABLE IF NOT EXISTS acl_sid
(
    id        bigint(20)   NOT NULL AUTO_INCREMENT,
    principal tinyint(1)   NOT NULL,
    sid       varchar(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY unique_uk_1 (sid, principal)
);

CREATE TABLE IF NOT EXISTS acl_class
(
    id    bigint(20)   NOT NULL AUTO_INCREMENT,
    class varchar(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY unique_uk_2 (class)
);

CREATE TABLE IF NOT EXISTS acl_entry
(
    id                  bigint(20) NOT NULL AUTO_INCREMENT,
    acl_object_identity bigint(20) NOT NULL,
    ace_order           int(11)    NOT NULL,
    sid                 bigint(20) NOT NULL,
    mask                int(11)    NOT NULL,
    granting            tinyint(1) NOT NULL,
    audit_success       tinyint(1) NOT NULL,
    audit_failure       tinyint(1) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY unique_uk_4 (acl_object_identity, ace_order)
);

CREATE TABLE IF NOT EXISTS acl_object_identity
(
    id                 bigint(20) NOT NULL AUTO_INCREMENT,
    object_id_class    bigint(20) NOT NULL,
    object_id_identity bigint(20) NOT NULL,
    parent_object      bigint(20) DEFAULT NULL,
    owner_sid          bigint(20) DEFAULT NULL,
    entries_inheriting tinyint(1) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY unique_uk_3 (object_id_class, object_id_identity)
);

ALTER TABLE acl_entry
    ADD FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id);

ALTER TABLE acl_entry
    ADD FOREIGN KEY (sid) REFERENCES acl_sid (id);

create table IF NOT EXISTS user_roles
(
    id   integer not null,
    user_id integer,
    role varchar(255),
    primary key (id)
);

--
-- Constraints for table acl_object_identity
--
ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id);

ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (object_id_class) REFERENCES acl_class (id);

ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (owner_sid) REFERENCES acl_sid (id);



CREATE TABLE conjunto_a (
                            id bigint    PRIMARY KEY AUTO_INCREMENT,
                            nombre VARCHAR(50),
                            descripcion VARCHAR(255)
);

CREATE TABLE conjunto_b (
                            id bigint  PRIMARY KEY AUTO_INCREMENT,
                            nombre VARCHAR(50),
                            descripcion VARCHAR(255),
                            conjunto_a_id bigint,
                            FOREIGN KEY (conjunto_a_id) REFERENCES conjunto_a(id)
);


CREATE TABLE conjunto_c (
                            id  bigint  PRIMARY KEY AUTO_INCREMENT,
                            nombre VARCHAR(50),
                            descripcion VARCHAR(255),
                            conjunto_b_id bigint,
                            FOREIGN KEY (conjunto_b_id) REFERENCES conjunto_b(id)
);

