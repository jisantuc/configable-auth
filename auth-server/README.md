`auth-server`
=============

This directory holds an auth microservice written in Typescript. It is a placeholder for a more sophisticated authentication service,
tl;dr:

- `nvm use`
- `npm install -g typescript`
- `npm install`
- `npm run dev`

The service has one valid token, so the only way to get user info back is:

```bash
$ echo '{"token": "good token"}' | http :4000/tokens
```

Any other request to `/tokens` will result in a 403.