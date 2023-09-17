CREATE TABLE IF NOT EXISTS USERS (
  user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_name VARCHAR(255) NOT NULL,
  user_email VARCHAR(255) NOT NULL,
  CONSTRAINT unique_email UNIQUE (user_email)
);

CREATE TABLE IF NOT EXISTS CATEGORIES (
    category_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    category_name VARCHAR NOT NULL,
    CONSTRAINT unique_category UNIQUE (category_name)
);

CREATE TABLE IF NOT EXISTS LOCATIONS (
  location_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  lat FLOAT NOT NULL,
  lon FLOAT NOT NULL,
);

CREATE TABLE IF NOT EXISTS EVENTS (
  event_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  event_title VARCHAR NOT NULL,
  annotation VARCHAR NOT NULL,
  description VARCHAR NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  paid BOOLEAN NOT NULL,
  participant_limit INTEGER NOT NULL,
  published_date TIMESTAMP WITHOUT TIME ZONE,
  request_moderation BOOLEAN NOT NULL,
  state VARCHAR NOT NULL,
  category_id BIGINT NOT NULL,
  location_id BIGINT NOT NULL,
  initiator_id BIGINT NOT NULL,
  CONSTRAINT fk_event_category_id FOREIGN KEY (category_id) REFERENCES category (category_id)
  CONSTRAINT fk_event_location_id FOREIGN KEY (location_id) REFERENCES location (location_id),
  CONSTRAINT fk_event_initiator_id FOREIGN KEY (initiator_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS PARTICIPATION_REQUESTS (
  participation_request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  created_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_id BIGINT NOT NULL,
  requester_id BIGINT NOT NULL,
  status VARCHAR NOT NULL,
  CONSTRAINT fk_participation_request_event_id FOREIGN KEY (event_id) REFERENCES event (event_id),
  CONSTRAINT fk_participation_request_requester_id FOREIGN KEY (requester_id) REFERENCES users (user_id),
  CONSTRAINT unique_category_event_id_requester_id UNIQUE (event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS COMPILATIONS (
  compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  compilation_title VARCHAR NOT NULL,
  compilation_pinned BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS EVENT_COMPILATION (
  compilation_id  BIGINT NOT NULL,
  event_id BIGINT NOT NULL,
  CONSTRAINT fk_compilation_id FOREIGN KEY (compilation_id) REFERENCES COMPILATIONS (compilation_id),
  CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES EVENTS (event_id),
  PRIMARY KEY (compilation_id, event_id)
);