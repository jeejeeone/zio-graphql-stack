CREATE TABLE IF NOT EXISTS test.address
(
    id UInt32,
    street String
)
    ENGINE = MergeTree()
ORDER BY id;

CREATE TABLE IF NOT EXISTS test.person
(
    id UInt32,
    name String,
    address_id UInt32
)
    ENGINE = MergeTree()
ORDER BY id;

INSERT INTO test.address (id, street) VALUES (1, '123 Maple Street'), (2, '456 Oak Avenue'), (3, '789 Pine Lane');

INSERT INTO test.person (id, name, address_id) VALUES (1, 'Alice', 1), (2, 'Bob', 2), (3, 'Charlie', 3);

CREATE VIEW test.person_address_tupled AS
SELECT
    p.id AS person_id,
    p.name AS person_name,
    tuple(a.id, a.street) AS address
FROM
    person p
        JOIN
    address a ON p.address_id = a.id;