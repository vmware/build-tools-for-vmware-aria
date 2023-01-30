###
# #%L
# abx-all
# %%
# Copyright (C) 2023 VMWARE
# %%
# This program is licensed under Technical Preview License by VMware.
# VMware shall own and retain all right, title and interest in and to the Intellectual Property Rights in the Technology Preview Software.
# ALL RIGHTS NOT EXPRESSLY GRANTED IN LICENSE ARE RESERVED TO VMWARE.
# VMware is under no obligation to support the Technology Preview Software in any way or to provide any Updates to Licensee.
# You should have received a copy of the Technical Preview License along with this program.  If not, see 
# <https://flings.vmware.com/vrealize-build-tools/license>
# #L%
###
import json

def handler(context, inputs):
    jsonOut=json.dumps(inputs, separators=(',', ':'))
    print("Inputs were {0}".format(jsonOut))

    print("Executing the ABX action from the Demo pacakge")
    print(context)
    return {"test": "testval"}

