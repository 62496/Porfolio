from odoo import models, fields, api
from odoo.exceptions import ValidationError

class HrFeedback(models.Model):
    _name = 'hr.feedback'
    _description = 'Interview Feedback'

    author_id = fields.Many2one(
        'res.users',
        string='Author',
        required=True,
        default=lambda self: self.env.user
    )

    description = fields.Text(string='Feedback')

    state = fields.Selection(
        [('draft', 'Draft'), ('done', 'Done')],
        default='draft',
        string='Status'
    )

    application_id = fields.Many2one(
        'hr.applicant',
        string='Application',
        required=True
    )

    questions_ids = fields.One2many(
        'hr.feedback.question',
        'feedback_id',
        string='Questions'
    )

    average_score = fields.Float(
        compute='_compute_average_score',
        string='Average Score'
    )

    questions_count = fields.Integer(
        compute='_compute_questions_count',
        string='Questions Count'
    )

    @api.depends('questions_ids.score')
    def _compute_average_score(self):
        for rec in self:
            scores = rec.questions_ids.mapped('score')
            rec.average_score = sum(scores) / len(scores) if scores else 0.0

    @api.depends('questions_ids')
    def _compute_questions_count(self):
        for rec in self:
            rec.questions_count = len(rec.questions_ids)

    @api.constrains('state', 'questions_ids')
    def _check_done_without_questions(self):
        for rec in self:
            if rec.state == 'done' and not rec.questions_ids:
                raise ValidationError(
                    "You cannot set feedback to Done without any question."
                )
    def action_done(self):
        for record in self:
            record.state = "done"


    def action_draft(self):
        for record in self:
            record.state = "draft"

    