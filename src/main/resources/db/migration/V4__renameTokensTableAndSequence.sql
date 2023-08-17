ALTER TABLE public.app_user ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.image ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.tokens ALTER COLUMN id DROP DEFAULT;

ALTER TABLE public.tokens RENAME TO activation_tokens;
ALTER SEQUENCE public.app_user_token_seq RENAME TO activation_token_sequence;
