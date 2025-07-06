ALTER TABLE public.activation_tokens ALTER COLUMN created_at TYPE timestamp without time zone;
ALTER TABLE public.activation_tokens ALTER COLUMN updated_at TYPE timestamp without time zone;
ALTER TABLE public.image ALTER COLUMN created_at TYPE timestamp without time zone;
ALTER TABLE public.image ALTER COLUMN updated_at TYPE timestamp without time zone;
ALTER TABLE public.mail_change_tokens ALTER COLUMN created_at TYPE timestamp without time zone;
ALTER TABLE public.mail_change_tokens ALTER COLUMN updated_at TYPE timestamp without time zone;
ALTER TABLE public.app_user ALTER COLUMN created_at TYPE timestamp without time zone;
ALTER TABLE public.app_user ALTER COLUMN updated_at TYPE timestamp without time zone;
