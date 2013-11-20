SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;

USE `mydb` ;

-- -----------------------------------------------------

-- Table `mydb`.`contact`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `mydb`.`contact` (

  `name` VARCHAR(30) NULL DEFAULT NULL ,

  `number` VARCHAR(20) NULL DEFAULT NULL ,

  `email` VARCHAR(45) NULL DEFAULT NULL ,

  `id` INT(11) NOT NULL AUTO_INCREMENT ,

  PRIMARY KEY (`id`) )

ENGINE = InnoDB

DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------

-- Table `mydb`.`user`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `mydb`.`user` (

  `id` INT(11) NOT NULL AUTO_INCREMENT ,

  `username` VARCHAR(20) NULL DEFAULT NULL ,

  `password` VARCHAR(45) NULL DEFAULT NULL ,

  `farmerid` INT(11) NULL DEFAULT NULL ,

 PRIMARY KEY (`id`) )

ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `mydb`.`farmer`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `mydb`.`farmer` (

  `id` INT(11) NOT NULL AUTO_INCREMENT ,

  `name` VARCHAR(40) NULL DEFAULT NULL ,

  `default_pos_x` DOUBLE NULL DEFAULT NULL ,

  `default_pos_y` DOUBLE NULL DEFAULT NULL ,

  `contact_id` INT(11) NULL DEFAULT NULL ,

  `email` VARCHAR(45) NULL DEFAULT NULL ,

  `number` VARCHAR(20) NULL DEFAULT NULL ,

  `user_id` INT(11) NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `fk_Farmer_Contact1_idx` (`contact_id` ASC) ,

  INDEX `fk_farmer_user1_idx` (`user_id` ASC) ,

  CONSTRAINT `fk_contact_id`

    FOREIGN KEY (`contact_id` )

    REFERENCES `mydb`.`contact` (`id` )

    ON DELETE NO ACTION

    ON UPDATE NO ACTION,

  CONSTRAINT `fk_farmer_user1`

    FOREIGN KEY (`user_id` )

    REFERENCES `mydb`.`user` (`id` )

    ON DELETE NO ACTION

    ON UPDATE NO ACTION)

ENGINE = InnoDB

DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------

-- Table `mydb`.`sheep`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `mydb`.`sheep` (

  `id` INT(11) NOT NULL AUTO_INCREMENT ,

  `mileage` DOUBLE NULL DEFAULT NULL ,

  `healthflags` INT(11) NULL DEFAULT NULL ,

  `pos_x` FLOAT NULL DEFAULT '0' ,

  `pos_y` FLOAT NULL DEFAULT '0' ,

  `farmerid` INT(11) NOT NULL ,

  `name` VARCHAR(45) NULL DEFAULT NULL ,

  `birthdate` BIGINT(20) NULL DEFAULT '0' ,

  `sex` VARCHAR(10) NULL DEFAULT 'f' ,

  `pulse` INT(11) NULL DEFAULT NULL ,

  `temperature` INT(11) NULL DEFAULT NULL ,

  `alive` INT(1) NULL DEFAULT '1' ,

  PRIMARY KEY (`id`) ,

  INDEX `fk_farmerid_idx` (`farmerid` ASC) ,

  CONSTRAINT `fk_parentid`

    FOREIGN KEY (`farmerid` )

    REFERENCES `mydb`.`farmer` (`id` )

    ON DELETE NO ACTION

    ON UPDATE NO ACTION)

ENGINE = InnoDB

DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------

-- Table `mydb`.`alarm`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `mydb`.`alarm` (

  `id` INT(11) NOT NULL AUTO_INCREMENT ,

  `alarmflags` VARCHAR(45) NULL DEFAULT NULL ,

  `sheepid` INT(11) NOT NULL ,

  `isactive` INT(1) NULL DEFAULT '1' ,

  `isshown` INT(1) NULL DEFAULT '0' ,

  PRIMARY KEY (`id`) ,

  INDEX `fk_sheepid_idx` (`sheepid` ASC) ,

  CONSTRAINT `fk_sheepid`

    FOREIGN KEY (`sheepid` )

    REFERENCES `mydb`.`sheep` (`id` )

    ON DELETE NO ACTION

    ON UPDATE NO ACTION)

ENGINE = InnoDB

DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------

-- Table `mydb`.`deadsheep`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `mydb`.`deadsheep` (

  `id` INT(11) NOT NULL AUTO_INCREMENT ,

  `sheepid` INT(11) NULL DEFAULT NULL ,

  `timeofdeath` BIGINT(20) NULL DEFAULT NULL ,

  `causeofdeath` INT(11) NULL DEFAULT NULL ,

  PRIMARY KEY (`id`) )

ENGINE = InnoDB

AUTO_INCREMENT = 100

DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------

-- Table `mydb`.`sheephistory`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `mydb`.`sheephistory` (

  `sheepid` INT(11) NOT NULL ,

  `pos_x` FLOAT NULL DEFAULT NULL ,

  `pos_y` FLOAT NULL DEFAULT NULL ,

  `timestamp` BIGINT(20) NULL DEFAULT NULL ,

  INDEX `fk_sheepforeignid_idx` (`sheepid` ASC) ,

  CONSTRAINT `fk_sheepforeignid`

    FOREIGN KEY (`sheepid` )

    REFERENCES `mydb`.`sheep` (`id` )

    ON DELETE NO ACTION

    ON UPDATE NO ACTION)

ENGINE = InnoDB

DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------

-- Table `mydb`.`sheepmedicalhistory`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `mydb`.`sheepmedicalhistory` (

  `id` INT(11) NOT NULL AUTO_INCREMENT ,

  `healthflag` BIGINT(20) NULL DEFAULT NULL ,

  `timestamp` BIGINT(20) NULL DEFAULT NULL ,

  `sheepid` INT(11) NULL DEFAULT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `fk_sheepid_medical_idx` (`sheepid` ASC) ,

  CONSTRAINT `fk_sheepid_medical`

    FOREIGN KEY (`sheepid` )

    REFERENCES `mydb`.`sheep` (`id` )

    ON DELETE NO ACTION

    ON UPDATE NO ACTION)

ENGINE = InnoDB

AUTO_INCREMENT = 6295

DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------

-- Table `mydb`.`state`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `mydb`.`state` (

  `id` INT(11) NOT NULL ,

  `value` BIGINT(20) NULL DEFAULT NULL ,

  PRIMARY KEY (`id`) )

ENGINE = InnoDB

DEFAULT CHARACTER SET = utf8;

USE `mydb` ;

SET SQL_MODE=@OLD_SQL_MODE;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;