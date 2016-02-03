#!/usr/bin/env python2.7

import common
import pdb
phone="18810346542"
passwd="888888"

def Register():
    code = common.GetCode("reg",phone,None)
    post = common.PostData("Register",{"phone":phone,"passwd":passwd,"code":code})
    response = common.HttpRequest(common.urls["register"],post)
    if common.ProcessResponse(response):
        return response["status"]
    else:
        return None

if __name__ == "__main__":
    Register()
