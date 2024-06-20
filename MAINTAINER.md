# Maintainer Notes

This file provides information on how to push new updates out.

## Main App

1. Commit and push changes
2. Create new tag (`git tag v0.0.0`)
3. Push tags (`git push --tags`)
4. Monitor GitHub Actions

## NPM

1. Create production build (`npm run build` from root directory)
2. Commit and push changes
3. Run `npm publish` from `npm` directory