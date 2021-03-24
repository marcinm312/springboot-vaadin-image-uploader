ALTER TABLE public.app_user ADD COLUMN email character varying(255);

ALTER TABLE public.app_user ADD COLUMN is_enabled boolean;
UPDATE public.app_user SET is_enabled = true;
ALTER TABLE public.app_user ALTER COLUMN is_enabled SET NOT NULL;

ALTER TABLE public.app_user ADD COLUMN created_at timestamp with time zone;
UPDATE public.app_user SET created_at = CURRENT_TIMESTAMP;
ALTER TABLE public.app_user ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE public.app_user ADD COLUMN updated_at timestamp with time zone;
UPDATE public.app_user SET updated_at = CURRENT_TIMESTAMP;
ALTER TABLE public.app_user ALTER COLUMN updated_at SET NOT NULL;


CREATE SEQUENCE public.app_user_token_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE public.tokens
(
    id bigint NOT NULL DEFAULT nextval('app_user_token_seq'::regclass),
    value character varying(255),
    user_id bigint,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT tokens_pkey PRIMARY KEY (id),
    CONSTRAINT tokens_fkey_user_id FOREIGN KEY (user_id)
        REFERENCES public.app_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE public.image ADD COLUMN created_at timestamp with time zone;
UPDATE public.image SET created_at = CURRENT_TIMESTAMP;
ALTER TABLE public.image ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE public.image ADD COLUMN updated_at timestamp with time zone;
UPDATE public.image SET updated_at = CURRENT_TIMESTAMP;
ALTER TABLE public.image ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE public.image ADD COLUMN user_id bigint;
UPDATE public.image SET user_id = (
    SELECT id
    FROM app_user
    WHERE username = 'administrator'
);
ALTER TABLE public.image ADD CONSTRAINT image_fkey_user_id FOREIGN KEY (user_id)
    REFERENCES public.app_user (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE;