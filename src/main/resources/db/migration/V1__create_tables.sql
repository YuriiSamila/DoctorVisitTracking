DROP TABLE IF EXISTS visits;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;
CREATE TABLE IF NOT EXISTS patients (
                                        id INT PRIMARY KEY AUTO_INCREMENT,
                                        first_name VARCHAR(50) NOT NULL,
                                        last_name VARCHAR(50) NOT NULL);

CREATE TABLE IF NOT EXISTS doctors (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       first_name VARCHAR(50) NOT NULL,
                                        last_name VARCHAR(50) NOT NULL,
                                        time_zone VARCHAR(50) NOT NULL);

CREATE TABLE IF NOT EXISTS visits (
                                      id INT PRIMARY KEY AUTO_INCREMENT,
                                      start_date_time DATETIME NOT NULL,
                                      end_date_time DATETIME NOT NULL,
                                      patient_id INT NOT NULL,
                                      doctor_id INT NOT NULL,
                                      FOREIGN KEY (patient_id) REFERENCES patients(id),
                                      FOREIGN KEY (doctor_id) REFERENCES doctors(id));