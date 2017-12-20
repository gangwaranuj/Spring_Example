import $ from 'jquery';
import { showFormMessaging } from './credentials-messaging';

const $passwordField = $('#password_new');
const $resetPasswordMessages = $('#reset-password-messages');

showFormMessaging($resetPasswordMessages);

$passwordField.focus();
