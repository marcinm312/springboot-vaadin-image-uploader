CREATE SEQUENCE public.app_user_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE public.image_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE public.app_user
(
    id bigint NOT NULL DEFAULT nextval('app_user_id_seq'::regclass),
    password character varying(255),
    role character varying(255),
    username character varying(50),
    CONSTRAINT app_user_pkey PRIMARY KEY (id),
    CONSTRAINT app_user_unique_username UNIQUE (username)
);

CREATE TABLE public.image
(
    id bigint NOT NULL DEFAULT nextval('image_id_seq'::regclass),
    image_address character varying(255),
    CONSTRAINT image_pkey PRIMARY KEY (id)
);