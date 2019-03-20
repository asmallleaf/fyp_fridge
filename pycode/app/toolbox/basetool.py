from abc import abstractmethod

class BaseTool():
    @abstractmethod
    def _help(self):
        print('this is a toolbox and will be enriched in the future\n')

    @abstractmethod
    def _helpfor(self,fnc_name):
        print('you can use this to get help() for specific function\n')

class BaseError(RuntimeError):
    def __init__(self,args):
        self.args = args

    def __str__(self):
        return self.args