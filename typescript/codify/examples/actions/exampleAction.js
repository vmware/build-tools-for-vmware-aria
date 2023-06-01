

/**
 * Example action
 * @vro_type         action
 * @vro_name         exampleAction
 * @vro_module       com.vmware.acoe.demo
 * @vro_input        {string} user User name
 * @vro_input        {number} age User's age
 * @vro_output       {Array/string} operation results
 */

/**
 * Greet the user
 * @param {string} person the person to greet.
 */
function greet(person) {
    System.log('Hello, ' + person);
}

/**
 * Output the user's age
 * @param {number} age the person's age.
 */
function logAge(age) {
    System.log('You are ' + age + ' years old');
}

greet(user);
logAge(age);

return ['foo', 'bar'];
