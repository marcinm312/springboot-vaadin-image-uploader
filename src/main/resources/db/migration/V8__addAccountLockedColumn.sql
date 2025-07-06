ALTER TABLE public.app_user ADD COLUMN is_account_non_locked boolean;
UPDATE public.app_user SET is_account_non_locked = true;
ALTER TABLE public.app_user ALTER COLUMN is_account_non_locked SET NOT NULL;
