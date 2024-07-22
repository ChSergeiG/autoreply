CREATE
    TABLE
        IF NOT EXISTS SETTING(
            ID UUID PRIMARY KEY,
            SETTING_KEY VARCHAR(128),
            SETTING_VALUE VARCHAR(1024)
        );

CREATE
    TABLE
        IF NOT EXISTS REPLIED_CHAT(
            ID UUID PRIMARY KEY,
            CHAT_ID BIGINT NOT NULL,
            REPLIED_TIME TIMESTAMP NOT NULL
        );

CREATE
    TABLE
        IF NOT EXISTS MESSAGE(
            ID UUID PRIMARY KEY,
            MESSAGE_ID BIGINT NOT NULL,
            CHAT_ID BIGINT NOT NULL,
            SENDER_ID BIGINT NOT NULL,
            MESSAGE_CONTENT TEXT NOT NULL,
            MESSAGE_TIME TIMESTAMP NOT NULL
        );

COMMENT ON
TABLE
    SETTING IS 'App settings store';

COMMENT ON
COLUMN SETTING.ID IS 'unique id';

COMMENT ON
COLUMN SETTING.SETTING_KEY IS 'unique setting key';

COMMENT ON
COLUMN SETTING.SETTING_VALUE IS 'SETTING_KEY setting value';

COMMENT ON
TABLE
    REPLIED_CHAT IS 'List of chats, that was already replied';

COMMENT ON
COLUMN REPLIED_CHAT.ID IS 'unique id';

COMMENT ON
COLUMN REPLIED_CHAT.CHAT_ID IS 'chat id that was already replied';

COMMENT ON
COLUMN REPLIED_CHAT.REPLIED_TIME IS 'last time CHAT_ID was replied';

COMMENT ON
TABLE
    MESSAGE IS 'List of messages, that was read from update listener';

COMMENT ON
COLUMN MESSAGE.ID IS 'unique id';

COMMENT ON
COLUMN MESSAGE.MESSAGE_ID IS 'id of saved message';

COMMENT ON
COLUMN MESSAGE.CHAT_ID IS 'chat id where this message have been read';

COMMENT ON
COLUMN MESSAGE.SENDER_ID IS 'message sended id';

COMMENT ON
COLUMN MESSAGE.MESSAGE_CONTENT IS 'json representation of content of message';

COMMENT ON
COLUMN MESSAGE.MESSAGE_TIME IS 'when this message was captured';
