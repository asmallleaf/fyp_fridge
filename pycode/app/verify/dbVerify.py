from app.toolbox.verifytool import VerifyTool as vt

class Verify(vt):

    @classmethod
    def check(cls,items,max,min,error_arg,error_tab):
        result,item_num = cls.checkNum(items,max_num=max,min_num=min)
        if result:
            return item_num
        else:
            vt.raiseVerifyError(error_arg,error_tab)
            return False

    @classmethod
    def check2(cls,items,val,upper,error_arg,error_tab):
        result,item_num = cls.checkNum2(items,order_num=val,is_upper=upper)
        if result:
            return item_num
        else:
            vt.raiseVerifyError(error_arg,error_tab)
            return False