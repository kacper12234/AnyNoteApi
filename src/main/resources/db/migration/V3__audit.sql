CREATE TABLE revinfo (
                         rev INT AUTO_INCREMENT PRIMARY KEY,
                         revtstmp BIGINT
);

CREATE TABLE items_aud (
                           id BINARY(16) NOT NULL,
                           rev INT NOT NULL,
                           revtype TINYINT,
                           owner_id BINARY(16),
                           title VARCHAR(255),
                           content TEXT,
                           version BIGINT,
                           deleted BOOLEAN,
                           created_at TIMESTAMP,
                           updated_at TIMESTAMP,
                           PRIMARY KEY (id, rev)
);