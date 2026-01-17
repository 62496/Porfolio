# -*- coding: utf-8 -*-
{
    'name': "hr_feedback",

    'summary': "feedback of Interview",

    'author': "Abdelrahman Meroual",
    'website': "https://www.yourcompany.com",

    # Categories can be used to filter modules in modules listing
    # Check https://github.com/odoo/odoo/blob/15.0/odoo/addons/base/data/ir_module_category_data.xml
    # for the full list
    'category': 'Uncategorized',
    'version': '0.1',

    # any module necessary for this one to work correctly   
    'depends': ['base', 'hr_recruitment', 'hr_skills'],
    'installable': True,
    'application': True,
    # always loaded
    'data': [
    'security/ir.model.access.csv',
    'views/hr_feedback_views.xml',
    'views/hr_feedback_question_views.xml',
    'views/hr_feedback_actions.xml',
    'views/hr_feedback_menu.xml',
    'views/hr_applicant_views.xml',

    

    ],
    # only loaded in demonstration mode
    'demo': [
        'demo/demo.xml',
    ],
}

