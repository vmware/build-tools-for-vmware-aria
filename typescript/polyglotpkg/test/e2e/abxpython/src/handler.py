def handler(context, inputs):
    print("Executing the ABX action from the Demo pacakge")
    print(context)
    print(inputs)
    return {"test": "testval"}
