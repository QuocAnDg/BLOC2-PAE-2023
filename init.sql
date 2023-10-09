DROP SCHEMA IF EXISTS pae CASCADE;
CREATE SCHEMA pae;

CREATE TABLE pae.photos
(
    photo_id    SERIAL PRIMARY KEY,
    access_path VARCHAR(255) NOT NULL,
    type        VARCHAR(20)  NOT NULL -- {'avatar', 'profile_picture', 'item_photo'}
);

CREATE TABLE pae.item_types
(
    type_id SERIAL PRIMARY KEY,
    name    VARCHAR(30) NOT NULL
);

CREATE TABLE pae.availabilities
(
    availability_id   SERIAL PRIMARY KEY,
    availability_date DATE NOT NULL
);

CREATE TABLE pae.users
(
    user_id           SERIAL PRIMARY KEY,
    last_name         VARCHAR(100)  NOT NULL,
    first_name        VARCHAR(100)  NOT NULL,
    email             VARCHAR(100)  NOT NULL,
    password          VARCHAR(100)  NOT NULL,
    phone_number      VARCHAR(20) NOT NULL,
    profile_picture   INTEGER       NOT NULL REFERENCES pae.photos,
    role              VARCHAR(7)    NOT NULL, -- {'user', 'helper', 'admin'}
    registration_date DATE          NOT NULL
);

CREATE TABLE pae.items
(
    item_id                SERIAL PRIMARY KEY,
    name                   VARCHAR(100) NOT NULL,
    type                   INTEGER      NOT NULL REFERENCES pae.item_types,
    description            VARCHAR(120) NOT NULL,
    photo                  INTEGER      NOT NULL REFERENCES pae.photos,
    price                  FLOAT,
    state                  VARCHAR(30)  NOT NULL, -- {'proposed', 'confirmed', 'denied', 'in_workshop', 'in_store', 'for_sale', 'sold', 'removed'}
    reason_of_refusal      VARCHAR(120),
    offering_member        INTEGER REFERENCES pae.users,
    phone_number           VARCHAR(20),
    meeting_date           INTEGER      NOT NULL REFERENCES pae.availabilities,
    time_slot              VARCHAR(15)  NOT NULL, -- {'daytime', 'evening'}
    decision_date          DATE,
    store_deposit_date     DATE,
    market_withdrawal_date DATE,
    selling_date           DATE
);

CREATE TABLE pae.notifications
(
    notification_id SERIAL PRIMARY KEY,
    concerned_item  INTEGER     NOT NULL REFERENCES pae.items,
    type            VARCHAR(10) NOT NULL, -- {'proposal', 'decision'}
    creation_date   DATE        NOT NULL
);

CREATE TABLE pae.individual_notifications
(
    notification_id INTEGER REFERENCES pae.notifications,
    concerned_user  INTEGER REFERENCES pae.users,
    isRead          BOOLEAN NOT NULL,
    PRIMARY KEY (notification_id, concerned_user)
);

INSERT INTO pae.photos VALUES (DEFAULT, 'MrRiez.png', 'avatar');
INSERT INTO pae.photos VALUES (DEFAULT, 'fred.png', 'avatar');
INSERT INTO pae.photos VALUES (DEFAULT, 'caro.png', 'avatar');
INSERT INTO pae.photos VALUES (DEFAULT, 'achil.png', 'avatar');
INSERT INTO pae.photos VALUES (DEFAULT, 'bazz.jpg', 'avatar');
INSERT INTO pae.photos VALUES (DEFAULT, 'Chaise-wooden-gbe3bb4b3a_1280.png', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'Fauteuil-sofa-g99f90fab2_1280.jpg', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'Secretaire.png', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'Vaisselle-plate-629970_1280.jpg', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'Fauteuil-couch-g0f519ec38_1280.png', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'Fauteuil-design-gee14e1707_1280.jpg', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'bar-890375_1920.jpg', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'Fauteuil-chair-g6374c21b8_1280.jpg', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'Vaisselle-Bol-bowl-469295_1280.jpg', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'theo.png', 'avatar');
INSERT INTO pae.photos VALUES (DEFAULT, 'LitEnfant-nursery-g9913b3b19_1280.jpg', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'PotEpices-pharmacy-g01563afff_1280.jpg', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'Vaisselle-Tassescup-1320578_1280.jpg', 'item_photo');
INSERT INTO pae.photos VALUES (DEFAULT, 'Charline.png', 'avatar');
INSERT INTO pae.photos VALUES (DEFAULT, 'MmeRiez.png', 'avatar');
INSERT INTO pae.photos VALUES (DEFAULT, 'Vaisselle-Bol-bowl-Remplace.png', 'item_photo');

INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-03-04');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-03-11');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-03-18');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-03-25');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-04-01');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-04-15');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-04-22');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-04-29');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-05-13');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-05-27');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-06-03');
INSERT INTO pae.availabilities VALUES (DEFAULT, '2023-06-17');

INSERT INTO pae.users VALUES (DEFAULT, 'Riez', 'Robert', 'bert.riez@gmail.be',
                              '$2a$10$yUIHjiSsbpVIMiK3PEUZAuqLsgGSl81cy2tCxDgyoUS1w1fdXKbgG',
                              '0477/96.85.47', 1, 'admin', '2022-03-01');

INSERT INTO pae.users VALUES (DEFAULT, 'Muise', 'Alfred', 'fred.muise@gmail.be',
                              '$2a$10$E/BgxTu58IgNyJAJtrT.ZuIT.P4DvYo4rjfOrERKOesBoSsU3C9hG',
                              '0476/96.36.26', 2, 'helper', '2022-03-01');

INSERT INTO pae.users VALUES (DEFAULT, 'Line', 'Caroline', 'caro.line@hotmail.com',
                              '$2a$10$Se0KCGkpmymT1AolXu2ZoeqPAZp/eQTYaLQSW2ujwyhPM.nAMciUe',
                              '0487/45.23.79', 3, 'user', '2022-03-01');

INSERT INTO pae.users VALUES (DEFAULT, 'Ile', 'Achille', 'ach.ile@gmail.com',
                              '$2a$10$Se0KCGkpmymT1AolXu2ZoeqPAZp/eQTYaLQSW2ujwyhPM.nAMciUe',
                              '0477/65.32.24', 4, 'user', '2022-03-01');

INSERT INTO pae.users VALUES (DEFAULT, 'Ile', 'Basile', 'bas.ile@gmail.be',
                              '$2a$10$Se0KCGkpmymT1AolXu2ZoeqPAZp/eQTYaLQSW2ujwyhPM.nAMciUe',
                              '0485/98.86.42', 5, 'user', '2022-03-01');

INSERT INTO pae.users VALUES (DEFAULT, 'Ile', 'Théophile', 'theo.phile@proximus.be',
                              '$2a$10$Se0KCGkpmymT1AolXu2ZoeqPAZp/eQTYaLQSW2ujwyhPM.nAMciUe',
                              '0488/35.33.89', 15, 'user', '2022-03-01');

INSERT INTO pae.users VALUES (DEFAULT, 'Line', 'Charles', 'charline@proximus.be',
                              '$2a$10$prAa.ekpiTgV9Qq.0LaE5uVWp3b.pKsPBDtP2W1Oj4a5MPIc2iTnq',
                              '0481 35 62 49', 19, 'user', '2022-03-01');

INSERT INTO pae.item_types VALUES (DEFAULT, 'Meuble');
INSERT INTO pae.item_types VALUES (DEFAULT, 'Table');
INSERT INTO pae.item_types VALUES (DEFAULT, 'Chaise');
INSERT INTO pae.item_types VALUES (DEFAULT, 'Fauteuil');
INSERT INTO pae.item_types VALUES (DEFAULT, 'Lit/Sommier');
INSERT INTO pae.item_types VALUES (DEFAULT, 'Matelas');
INSERT INTO pae.item_types VALUES (DEFAULT, 'Couvertures');
INSERT INTO pae.item_types VALUES (DEFAULT, 'Matériel de cuisine');
INSERT INTO pae.item_types VALUES (DEFAULT, 'Vaisselle');

/*1*/
INSERT INTO pae.items VALUES (DEFAULT, 'Chaise en bois brut avec cousin beige',
                              3, 'Chaise en bois brut avec cousin beige', 6, 2.0, 'for_sale', NULL, 5, NULL, 3,'daytime',
                              '2023-03-15', '2023-03-23', NULL, NULL);

/*2*/
INSERT INTO pae.items VALUES (DEFAULT, 'Canapé 3 places blanc',
                              4, 'Canapé 3 places blanc', 7, 3.0,
                              'sold', NULL, 5, NULL, 3, 'daytime',
                              '2023-03-15', '2023-03-23', NULL, '2023-03-23');
/*3*/
INSERT INTO pae.items VALUES (DEFAULT, 'Secrétaire',
                              1, 'Secrétaire', 8, NULL,
                              'denied',
                              'Ce meuble est magnifique mais fragile pour l’usage qui en sera fait',
                              NULL , '0496 32 16 54', 4, 'evening',
                              NULL, NULL, NULL, NULL);
/*4*/
INSERT INTO pae.items VALUES (DEFAULT, '100 assiettes blanches',
                              9, '100 assiettes blanches', 9, NULL,
                              'in_store', NULL, 4 , NULL, 4, 'evening',
                              '2023-03-20', '2023-03-29', NULL, NULL);
/*5*/
INSERT INTO pae.items VALUES (DEFAULT, 'Grand canapé 4 places bleu usé',
                              4, 'Grand canapé 4 places bleu usé', 10, 3.5,
                              'sold', NULL, 4 , NULL, 4, 'evening',
                              '2023-03-20', '2023-03-29', NULL, '2023-03-29');
/*6*/
INSERT INTO pae.items VALUES (DEFAULT, 'Fauteuil design très confortable',
                              4, 'Fauteuil design très confortable', 11, 5.2,
                              'removed', NULL, 4 , NULL, 3, 'evening',
                              '2023-03-15', '2023-03-18', '2023-04-29', NULL);
/*7*/
INSERT INTO pae.items VALUES (DEFAULT, 'Tabouret de bar en cuir',
                              3, 'Tabouret de bar en cuir', 12, NULL,
                              'denied', 'Ceci n’est pas une chaise', 4 , NULL, 5, 'evening',
                              NULL, NULL, NULL, NULL);
/*8*/
INSERT INTO pae.items VALUES (DEFAULT, 'Fauteuil ancien, pieds et accoudoir en bois',
                              4, 'Fauteuil ancien, pieds et accoudoir en bois', 13, NULL,
                              'in_workshop', NULL, 5 , NULL, 7, 'daytime',
                              '2023-04-11', NULL, NULL, NULL);
/*9*/
INSERT INTO pae.items VALUES (DEFAULT, '6 bols à soupe',
                              9, '6 bols à soupe', 14, NULL,
                              'in_store', NULL, 5 , NULL, 7, 'daytime',
                              '2023-04-11', '2023-04-25', NULL, NULL);

/*10*/
INSERT INTO pae.items VALUES (DEFAULT, 'Lit cage blanc',
                              1, 'Lit cage blanc', 16, NULL,
                              'in_store', NULL, 6 , NULL, 7, 'evening',
                              '2023-04-11', '2023-04-25', NULL, NULL);
/*11*/
INSERT INTO pae.items VALUES (DEFAULT, '30 pots à épices',
                              9, '30 pots à épices', 17, NULL,
                              'in_store', NULL, 6 , NULL, 8, 'daytime',
                              '2023-04-18', '2023-05-05', NULL, NULL);
/*12*/
INSERT INTO pae.items VALUES (DEFAULT, '4 tasses à café et leurs sous-tasses',
                              9, '4 tasses à café et leurs sous-tasses', 18, NULL,
                              'in_store', NULL, 3 , NULL, 8, 'daytime',
                              '2023-04-18', '2023-05-5', NULL, NULL);

ALTER TABLE pae.items
    ADD version INTEGER NOT NULL DEFAULT 1;

ALTER TABLE pae.users
    ADD version INTEGER NOT NULL DEFAULT 1;

-- Etats (en format lisible par le client) et comptage du nombre d’objets dans chacun des états.
SELECT state, COUNT(item_id)
FROM pae.items
GROUP BY state;

-- Comptage du nombre de types d’objets.
SELECT t.name AS type_name, COUNT(i.item_id)
FROM pae.item_types t
         LEFT OUTER JOIN pae.items i ON i.type = t.type_id
GROUP BY t.type_id, t.name;

-- Utilisateurs qui ont des objets et comptage du nombre d’objets par utilisateur
SELECT u.user_id, u.first_name, u.last_name, COUNT(i.item_id) as nombre_items
FROM pae.users u
         JOIN pae.items i ON i.offering_member = u.user_id
GROUP BY u.user_id, u.first_name, u.last_name;


-- Objets : description, état, date réception, date acceptation proposition, date dépôt en
-- magasin, date vente, date retrait & autres dates si précisées dans votre analyse.
SELECT
    description,
    state AS etat,
    decision_date AS acceptation_proposition_date,
    store_deposit_date AS date_de_depot,
    selling_date AS date_de_vente,
    market_withdrawal_date AS date_de_retrait
FROM pae.items;

-- Objets : comptage nombre de chaque date.
SELECT decision_date, COUNT(*)
FROM pae.items
WHERE decision_date IS NOT NULL
GROUP BY decision_date
ORDER BY decision_date;

SELECT store_deposit_date, COUNT(*)
FROM pae.items
WHERE store_deposit_date IS NOT NULL
GROUP BY store_deposit_date
ORDER BY store_deposit_date;

SELECT market_withdrawal_date, COUNT(*)
FROM pae.items
WHERE market_withdrawal_date IS NOT NULL
GROUP BY market_withdrawal_date
ORDER BY market_withdrawal_date;

SELECT selling_date, COUNT(*)
FROM pae.items
WHERE selling_date IS NOT NULL
GROUP BY selling_date
ORDER BY selling_date;

-- Rôles et comptage du nombre d’utilisateurs par rôle.
SELECT role, COUNT(role)
FROM pae.users
GROUP BY role;

