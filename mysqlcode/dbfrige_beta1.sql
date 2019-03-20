CREATE DATABASE db_fridge;
USE db_fridge;
#create table users
CREATE TABLE users(
	id INT NOT NULL AUTO_INCREMENT,
	PRIMARY KEY(id),
	user_name VARCHAR(20) NOT NULL,
	user_passwd CHAR(255) NOT NULL,
	fridge_code CHAR(8) NOT NULL UNIQUE,
	token TIMESTAMP
)ENGINE=INNODB DEFAULT CHARSET=utf8;
#create table fridges
CREATE TABLE fridges(
	id INT NOT NULL AUTO_INCREMENT,
	PRIMARY KEY(id),
	fridge_code CHAR(8) NOT NULL,
	FOREIGN KEY(fridge_code) REFERENCES users(fridge_code)
	ON UPDATE CASCADE ON DELETE CASCADE,
	item_list_code CHAR(8) NOT NULL UNIQUE,
	inf_show BOOLEAN NOT NULL
)ENGINE=INNODB DEFAULT CHARSET=utf8;
#create table items
CREATE TABLE items(
	id INT NOT NULL AUTO_INCREMENT,
	PRIMARY KEY(id),
	item_list_code CHAR(8) NOT NULL,
	FOREIGN KEY(item_list_code) REFERENCES fridges(item_list_code)
	ON UPDATE CASCADE ON DELETE CASCADE,
	item_name VARCHAR(20) DEFAULT"other",
	item_num INT DEFAULT"1",
	storage_time TIMESTAMP,
	tab VARCHAR(20)
)ENGINE=INNODB DEFAULT CHARSET=utf8;
#create table fridge information
CREATE TABLE fridge_inf(
	id INT NOT NULL AUTO_INCREMENT,
	PRIMARY KEY(id),
	fridge_code CHAR(8) NOT NULL UNIQUE,
	FOREIGN KEY(fridge_code) REFERENCES users(fridge_code),
	is_show_inf BOOLEAN NOT NULL,
	temperature FLOAT DEFAULT"0.0"
)ENGINE=INNODB DEFAULT CHARSET=utf8;
#create trigger for is show information col in fridges
DELIMITER ;;
CREATE TRIGGER inf_trigger AFTER UPDATE ON fridges
FOR EACH ROW
BEGIN
	UPDATE fridge_inf SET is_show_inf=fridges.`inf_show`
	WHERE fridges.`fridge_code`=fridge_inf.`fridge_code`;
END;;
DELIMITER ;


#create procedure for fridges table
DELIMITER ;;
CREATE PROCEDURE clearItemListCode(IN mcode CHAR(8))
BEGIN
	DECLARE temp_id INT DEFAULT -1;
	SELECT id INTO temp_id FROM items WHERE items.`item_list_code` = mcode;
	IF temp_id < 1 THEN
	DELETE FROM fridges WHERE fridges.`item_list_code` = mcode;
	END IF;
END;;
DELIMITER ;
#create trigger for items table

CREATE TRIGGER deletecode AFTER DELETE ON items
FOR EACH ROW CALL clearItemListCode(old.item_list_code);

INSERT INTO users(user_name,user_passwd,fridge_code) VALUES("homolive","12345","3");
INSERT INTO fridges(fridge_code,item_list_code,inf_show) VALUES("3","101","1");
INSERT INTO items(item_list_code,item_name,item_num,tab) VALUES("101","mazuri","10","vtuber");
#drop table items;
#drop database db_fridge;
#drop table fridge_inf;
#drop table fridges;
#update items set item_num=0 where item_list_code = 101 and item_name = "mazuri";
#UPDATE items SET item_num=1 WHERE item_list_code = 101 AND item_name = "mazuri";
#DELETE FROM items WHERE items.`item_list_code`= "101";