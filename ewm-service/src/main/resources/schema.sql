CREATE TABLE IF NOT EXISTS categories(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(150) NOT NULL,

CONSTRAINT pk_categories_unique UNIQUE (name),
CONSTRAINT pk_categories_primary_key PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(300) NOT NULL,

CONSTRAINT pk_users_id PRIMARY KEY (id),
CONSTRAINT pk_unique_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS events(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(512) NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests INT NOT NULL,
    created_on TIMESTAMP NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INT NOT NULL,
    published_on TIMESTAMP,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(20) NOT NULL,
    title VARCHAR(120) NOT NULL,
    views INT NOT NULL,

CONSTRAINT pk_events_id PRIMARY KEY (id),
CONSTRAINT pk_events_category_id FOREIGN KEY (category_id) REFERENCES categories(id),
CONSTRAINT pk_events_users_id FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS requests(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created TIMESTAMP NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,

CONSTRAINT pk_requests_id PRIMARY KEY (id),
CONSTRAINT pk_requests_events_id FOREIGN KEY (event_id) REFERENCES events(id),
CONSTRAINT pk_requests_users_id FOREIGN KEY (requester_id) REFERENCES users(id),
CONSTRAINT pk_requests_unique UNIQUE (event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(120) NOT NULL,
    pinned BOOLEAN NOT NULL,
CONSTRAINT pk_compilations_id PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilations_events(
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
CONSTRAINT pk_compilations_events_comp_id FOREIGN KEY (compilation_id) REFERENCES compilations(id),
CONSTRAINT pk_compilations_events_event_id FOREIGN KEY (event_id) REFERENCES events(id)
);