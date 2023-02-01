export async function handler (context: any, inputs: any, callback: Function) {

    try {
        await delay(() => {
            console.log('Inputs were ' + JSON.stringify(inputs));
        })
        callback(null, {status: "done"});

    } catch (err) {
        callback(new Error(err.message));
    }

}

async function delay(func: () => void, ms: number = 1) {
    await new Promise((resolve, reject) => {
        setTimeout(() => {
            try {
                func();
                resolve();
            } catch (e) {
                reject(e.message);
            }
        }, ms);
    });
}