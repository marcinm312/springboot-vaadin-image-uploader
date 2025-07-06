UPDATE public.image SET user_id = (
    SELECT id
    FROM public.app_user
    WHERE username = 'admin'
    LIMIT 1
)
WHERE user_id IS NULL;
