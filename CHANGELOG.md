# Changelog

_This changelog follows the [Common Changelog](https://common-changelog.org/) conventions._

## [0.3.0] - 2024-06-21

### Changed

- Form metadata is now hidden behind a warning to encourage use of input settings forms

### Added

- Plugins can now attach setting forms to custom inputs. You will need to remove and re-add custom inputs for the settings button to show.
- Added description field to text inputs
- **NPM package**: Added `handler`, `onChange`, and `onSubmit` functions to the form components to allow for custom submission handling
- **NPM package**: Added `defaults` property

## [0.2.0] - 2024-06-19

### Changed

- Emails that are too long to display on one line on the forms page will now be truncated. Hovering over it will show the full email.

### Added

- Added account permissions
- Added OpenID Connect SSO

### Fixed

- Fixed the background of some buttons not changing colour on hover

## [0.1.4] - 2024-05-14

### Changed

- Properly format messages and message ID header

## [0.1.3] - 2024-05-09

### Changed

- The `Form#recursivelyGetInputs` API method is no longer for internal use only

### Added

- Created a ReCAPTCHA plugin

### Fixed

- The SnowCaptcha plugin will now check if a form uses SnowCaptcha before attempting to validate the captcha token
- When configuring a button in the dashboard, the `button` type will now be selected by default to match the server-side default

## [0.1.2] - 2024-05-09

### Added

- Added logo on login page
- Added icon in browser tab

## [0.1.1] - 2024-05-09

### Changed

- Blank input values are no longer included in the email
- The Reply-To email now shows the user's name

### Fixed

- Fixed regular expression matching for partial matches

## [0.1.0] - 2024-05-07

### Added

- Added `getForms()` method to API
- Added `getForm(String id)` method to API
- Added `createForm(Form form)` method to API
- Added `editForm(Form form)` method to API
- Documented API using Javadoc

## [0.0.3] - 2024-05-06

### Fixed

- Inputs in the Multiple input are now sortable