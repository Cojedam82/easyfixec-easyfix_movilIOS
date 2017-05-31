# -*- coding: utf-8 -*-
# Generated by Django 1.11.1 on 2017-05-29 15:38
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0002_auto_20170518_1830'),
    ]

    operations = [
        migrations.AlterField(
            model_name='profile',
            name='role',
            field=models.PositiveSmallIntegerField(blank=True, choices=[(1, 'Cliente'), (2, 'Proveedor')], default=1, null=True, verbose_name='Role'),
        ),
        migrations.AlterField(
            model_name='reservation',
            name='status',
            field=models.PositiveIntegerField(blank=True, choices=[(1, 'Asignada'), (2, 'Cancelada'), (3, 'Pendiente'), (4, 'No Realizada'), (5, 'Realizada')], default=3, verbose_name='Status'),
        ),
    ]
