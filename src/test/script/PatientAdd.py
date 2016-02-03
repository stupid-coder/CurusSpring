#!/usr/bin/evn python2.7

import common
import Login

patient_urls = {
    "preadd":"http://localhost:8080/patient/preadd",
    "add":"http://localhost:8080/patient/add"
}

def PatientPreAdd(token,phone,id_number):
    post = common.PostData("PatientPreAdd",{"token":token,"id_number":id_number,"phone":phone})
    response = common.HttpRequest(patient_urls["preadd"],post)
    if common.ProcessResponse(response):
        return response["data"]["code"]
    else:
        return None

def PatientAdd(token,code,phone,id_number,appellation,name,gender,birth,address,weight,height):
    post = common.PostData("PatientAdd",{"token":token,"code":code,"phone":phone,"id_number":id_number,"appellation":appellation,"name":name,"address":address,"weight":weight,"height":height,"gender":gender,"birth":birth})
    response = common.HttpRequest(patient_urls["add"],post)
    if common.ProcessResponse(response):
        return response["data"]["patient_id"]
    else:
        return None

if __name__ == "__main__":
    phone="18810346544"
    id_number="41141111111113"
    token = Login.Login("18810346542","888888")
    code = PatientPreAdd(token,phone,id_number)
    PatientAdd(token,code,phone,id_number,"fu","qixiang2","2","1454470112","test",100,180)
