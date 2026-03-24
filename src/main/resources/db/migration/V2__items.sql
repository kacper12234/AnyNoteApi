CREATE TABLE items
(
    id         BINARY(16)   NOT NULL,
    owner_id   BINARY(16)   NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT,
    version    INT          NOT NULL,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_item_owner FOREIGN KEY (owner_id) REFERENCES users (id),
    INDEX idx_owner (owner_id)
);

CREATE TABLE item_permissions
(
    id        BINARY(16)                NOT NULL,
    item_id   BINARY(16)                NOT NULL,
    user_id   BINARY(16)                NOT NULL,
    role      ENUM('VIEWER', 'EDITOR')  NOT NULL,
    CONSTRAINT pk_item_permission PRIMARY KEY (id),
    CONSTRAINT fk_item_permission_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_item_permission_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_item_permission UNIQUE (item_id, user_id)
)