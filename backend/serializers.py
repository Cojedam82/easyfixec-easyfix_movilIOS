from rest_framework import serializers, status
from rest_framework.response import Response
from .models import *


class ProfileSerializer(serializers.ModelSerializer):

    class Meta:
        model = Profile
        fields = ('phone', 'role', 'token')
        read_only_fields = ('role', 'token')


class UserSerializer(serializers.ModelSerializer):
    profile = ProfileSerializer()

    class Meta:
        model = User
        fields = ('id', 'first_name', 'last_name', 'email', 'password', 'profile')
        extra_kwargs = {
            'password': {
                'write_only': True,
                'required': False
            },
        }

    def update(self, instance, validated_data):
        for attr, value in validated_data.items():
            if attr == 'password':
                if value:
                    instance.set_password(value)
            elif attr == 'profile':
                instance.profile(**validated_data['profile'])
                #for attr2, value2 in validated_data['profile'].items():
                #    setattr(instance.profile, attr2, value2)
                instance.profile.save()
            else:
                setattr(instance, attr, value)
        instance.save()
        return instance


class CategorySerializer(serializers.ModelSerializer):

  class Meta:
      model = Category
      depth = 1
      fields = ('id', 'name', 'services')


class ServiceSerializer(serializers.ModelSerializer):
  class Meta:
      model = Service
      fields = ('id', 'name', 'image')
