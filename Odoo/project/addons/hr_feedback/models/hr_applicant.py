import uuid
from odoo import models, fields

class HrApplicant(models.Model):
    _inherit = 'hr.applicant'

    feedback_ids = fields.One2many(
        'hr.feedback',
        'application_id',
        string='Feedbacks'
    )

    feedback_count = fields.Integer(
        string='Feedback Count',
        compute='_compute_feedback_count'
    )

    access_token = fields.Char(
        string='Access Token',
        default=lambda self: uuid.uuid4().hex,
        copy=False,
        readonly=True,
    )

    def _compute_feedback_count(self):
        for rec in self:
            rec.feedback_count = len(rec.feedback_ids)

    def regenerate_access_token(self):
        for rec in self:
            rec.access_token = uuid.uuid4().hex
            # return True so button feedback works
        return True
