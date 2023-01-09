import json

def handler(context, inputs):
    jsonOut=json.dumps(inputs, separators=(',', ':'))
    print("Inputs were {0}".format(jsonOut))

    outputs = {
      "status": "done"
    }

    return outputs
