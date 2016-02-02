#!/usr/bin/env python2.7

import common
import Login
import pdb

def PatientList(token):
    pdb.set_trace()
    post = common.PostData("PatientList",{"token":token})
    response = common.HttpRequest(common.urls["list"],post)
    if common.ProcessResponse(response):
        return response["data"]
    else:
        return None
    

if __name__ == "__main__":
    print("[INFO] PatientLIst: {}".format(PatientList(Login.Login())))
