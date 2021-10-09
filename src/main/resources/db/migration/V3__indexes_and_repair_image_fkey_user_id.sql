ALTER TABLE public.image DROP CONSTRAINT image_fkey_user_id;
ALTER TABLE public.image ADD CONSTRAINT image_fkey_user_id FOREIGN KEY (user_id)
    REFERENCES public.app_user (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE public.app_user ALTER COLUMN username SET NOT NULL;
CREATE UNIQUE INDEX username_idx ON public.app_user (username);