/*-
 * #%L
 * abx-all
 * %%
 * Copyright (C) 2023 VMWARE
 * %%
 * This program is licensed under Technical Preview License by VMware.
 * VMware shall own and retain all right, title and interest in and to the Intellectual Property Rights in the Technology Preview Software.
 * ALL RIGHTS NOT EXPRESSLY GRANTED IN LICENSE ARE RESERVED TO VMWARE.
 * VMware is under no obligation to support the Technology Preview Software in any way or to provide any Updates to Licensee.
 * You should have received a copy of the Technical Preview License along with this program.  If not, see
 * <https://flings.vmware.com/vrealize-build-tools/license>
 * #L%
 */
export async function handler(context: any, inputs: any, callback: Function) {

	try {
		await delay(() => {
			console.log('Inputs were ' + JSON.stringify(inputs));
		})
		callback(null, { 'done': "doneval" });
	} catch (err) {
		callback(new Error(err.message));
	}

}

async function delay(func: () => void, ms: number = 1) {
	await new Promise((resolve, reject) => {
		setTimeout(() => {
			try {
				func();
				resolve(undefined);
			} catch (e) {
				reject(e.message);
			}
		}, ms);
	});
}
