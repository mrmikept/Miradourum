CREATE TABLE `User` (ID int(10) NOT NULL AUTO_INCREMENT, Email varchar(255), Username varchar(255), Password varchar(255), Premium bit(1), PremiumEndDate date, PRIMARY KEY (ID));
CREATE TABLE PontoInteresse (ID int(10) NOT NULL AUTO_INCREMENT, Coordinates varchar(255), Name varchar(255), Description varchar(255), Dificulty int(10), Accessibility bit(1), State bit(1), Score double, PRIMARY KEY (ID));
CREATE TABLE Review (ID int(10) NOT NULL AUTO_INCREMENT, Comment varchar(255), Rating int(10), CreationDate date, UserID int(10) NOT NULL, PontoInteresseID int(10) NOT NULL, PRIMARY KEY (ID));
CREATE TABLE Image (ID int(10) NOT NULL AUTO_INCREMENT, Url varchar(255), ReviewID int(10) NOT NULL, PRIMARY KEY (ID));
CREATE TABLE PontoInteresse_User (PontoInteresseID int(10) NOT NULL, UserID int(10) NOT NULL, PRIMARY KEY (PontoInteresseID, UserID));
ALTER TABLE Review ADD CONSTRAINT FKReview970746 FOREIGN KEY (UserID) REFERENCES `User` (ID);
ALTER TABLE Image ADD CONSTRAINT FKImage267607 FOREIGN KEY (ReviewID) REFERENCES Review (ID);
ALTER TABLE Review ADD CONSTRAINT FKReview616691 FOREIGN KEY (PontoInteresseID) REFERENCES PontoInteresse (ID);
ALTER TABLE PontoInteresse_User ADD CONSTRAINT FKPontoInter992724 FOREIGN KEY (PontoInteresseID) REFERENCES PontoInteresse (ID);
ALTER TABLE PontoInteresse_User ADD CONSTRAINT FKPontoInter594713 FOREIGN KEY (UserID) REFERENCES `User` (ID);


-- Insert Users
INSERT INTO `User` (Email, Username, Password, Premium, PremiumEndDate) VALUES
('alice@example.com', 'alice', 'hashedpassword1', b'1', '2025-12-31'),
('bob@example.com', 'bob', 'hashedpassword2', b'0', NULL),
('charlie@example.com', 'charlie', 'hashedpassword3', b'1', '2025-06-30');

-- Insert Points of Interest
INSERT INTO PontoInteresse (Coordinates, Name, Description, Dificulty, Accessibility, State, Score) VALUES
('40.7128,-74.0060', 'Central Park', 'A large public park in NYC.', 2, b'1', b'1', 4.5),
('48.8584,2.2945', 'Eiffel Tower', 'Famous landmark in Paris.', 3, b'1', b'1', 4.8),
('35.6895,139.6917', 'Mount Takao', 'Popular hiking trail near Tokyo.', 4, b'0', b'1', 4.2);

-- Insert Reviews
INSERT INTO Review (Comment, Rating, CreationDate, UserID, PontoInteresseID) VALUES
('Beautiful and relaxing place!', 5, '2025-04-01', 1, 1),
('Great view, but very crowded.', 4, '2025-04-03', 2, 2),
('Challenging hike, but worth it!', 5, '2025-04-05', 3, 3);

-- Insert Images
INSERT INTO Image (Url, UserID, ReviewID) VALUES
('https://example.com/img1.jpg', 1, 1),
('https://example.com/img2.jpg', 2, 2),
('https://example.com/img3.jpg', 3, 3);

-- Insert PontoInteresse_User (Saved Points)
INSERT INTO PontoInteresse_User (PontoInteresseID, UserID) VALUES
(1, 1),
(2, 1),
(3, 2),
(1, 3);


