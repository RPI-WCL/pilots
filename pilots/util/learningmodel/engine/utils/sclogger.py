"""
Logger for the learninigmodel project
"""
import logging
import sys

# will there be any problem when this file is being imported multiple times?
_ST_SCLOG = "sc_learn_model"
_ST_SCLOG_FILE = "sclog.debug.log"
SCLOG = logging.getLogger(_ST_SCLOG)
def init():
    """
    Initialize the logger
    """
    _SCLOG_DEBUG = logging.FileHandler("sclog.debug.log")
    _SCLOG_DEBUG.setLevel(logging.DEBUG)
    _SCLOG_ERROR = logging.StreamHandler()
    _SCLOG_ERROR.setLevel(logging.ERROR)
    _SCLOG_FORMAT = logging.Formatter('%(levelname)s-%(message)s')
    _SCLOG_DEBUG.setFormatter(_SCLOG_FORMAT)
    _SCLOG_ERROR.setFormatter(_SCLOG_FORMAT)
    SCLOG.addHandler(_SCLOG_DEBUG)
    SCLOG.addHandler(_SCLOG_ERROR)
